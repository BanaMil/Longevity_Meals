// screen_change_address.dart

/* 
회원가입할 때 입력된 주소는 그대로 flutter에 저장된 거 가져오기
추가로 입력된 주소도 backend에 저장하고 flutter에도 저장하기
만약 다른 주소로 선택해서 변경하면 auth_service에서 API 호출하기
*/
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:kpostal/kpostal.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:frontend/services/user_storage.dart';
import 'package:frontend/services/service_auth.dart';
import 'package:flutter/foundation.dart'; 

// 파일 상단(클래스 밖) 또는 클래스 내부에 헬퍼 추가
void logi(String message, {Object? error, StackTrace? stackTrace}) {
  if (kDebugMode) {
    if (error != null) {
      debugPrint('$message\nERROR: $error');
      if (stackTrace != null) debugPrint('$stackTrace');
    } else {
      debugPrint(message);
    }
  }
}

class ChangeAddressScreen extends StatefulWidget {
  final String userid;
  const ChangeAddressScreen({super.key, required this.userid});

  @override
  State<ChangeAddressScreen> createState() => _ChangeAddressScreenState();
}

class _ChangeAddressScreenState extends State<ChangeAddressScreen> {
  AddressItem? _current; // 현재 사용 중인 주소
  final List<AddressItem> _address = []; // 저장된 주소 목록
  int? _selectedIndex; // 목록에서 사용자 선택 인덱스
  bool _loading = true;

  static String _listKey(String userid) => 'addresses_$userid';

  @override
  void initState() {
    super.initState();
    _bootstrap();
  }

  Future<void> _bootstrap() async {
    try {
      final prefs = await SharedPreferences.getInstance();

      final user = await UserStorage.loadUserInfo();
      // 1) 프리뷰 우선, 없으면 조합
      final preview =
          (user['address'] != null && user['address']!.trim().isNotEmpty)
              ? user['address']!.trim()
              : UserStorage.buildPreviewAddress(
                  addressRoad: user['addressRoad'],
                  addressDetail: user['addressDetail'],
                );

      final current = AddressItem(
        addressRoad: preview,
        addressJibun: user['addressJibun'],
        postCode: user['postCode'],
        addressDetail: user['addressDetail'],
      );

      // 저장된 주소 목록 로딩
      final listJson = prefs.getString(_listKey(widget.userid));
      final list = (listJson == null || listJson.isEmpty)
          ? <AddressItem>[]
          : (jsonDecode(listJson) as List<dynamic>)
              .map((e) => AddressItem.fromJson(e as Map<String, dynamic>))
              .toList();

      // 목록에 현재 프리뷰가 없으면 넣어줌
      if (current.addressRoad.isNotEmpty &&
          !list.any((a) => a.sameAs(current))) {
        list.insert(0, current);
      }

      setState(() {
        _current = current;
        _address
          ..clear()
          ..addAll(list);
        _selectedIndex = null;
        _loading = false;
      });

      await _persistList();
    } catch (e) {
      setState(() => _loading = false);
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text('주소 로딩 실패: $e')));
      }
    }
  }

  Future<void> _persistList() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonList = jsonEncode(_address.map((a) => a.toJson()).toList());
    await prefs.setString(_listKey(widget.userid), jsonList);
  }

  Future<void> _addAddressViaKpostal() async {
    final result = await Navigator.push<Kpostal>(
      context,
      MaterialPageRoute(
        builder: (_) => KpostalView(
          appBar: AppBar(title: const Text('주소 검색')),
        ),
      ),
    );

    if (result == null) {
      logi('🚫 Kpostal 닫힘: 선택된 주소 없음');
      return;
    }

    final newItem = AddressItem(
      addressRoad: result.address,
      addressJibun: result.jibunAddress,
      postCode: result.postCode,
      addressDetail: null,
    );

    setState(() {
      // 중복 방지
      if (!_address.any((a) => a.sameAs(newItem))) {
        _address.add(newItem);
        logi('➕ 리스트에 주소 추가: ${newItem.toJson()}');
      } else {
        logi('↩️ 중복으로 추가 스킵: ${newItem.toJson()}');
      }
    });
    await _persistList();
    logi('💾 주소 목록 저장 완료(개수=${_address.length})');
  }

  Future<void> _changeToSelected() async {
    if (_selectedIndex == null) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('변경할 주소를 선택하세요.')));
      return;
    }

    final target = _address[_selectedIndex!];

    // 🔎 변경 대상 로그
    logi('📝 변경할 주소 선택: idx=$_selectedIndex, data=${target.toJson()}');

    setState(() => _loading = true);
    try {
      // (백엔드) 주소 변경 API
      logi('🌐 PUT 주소 변경 요청 시작: userid=${widget.userid}');
      final response = await AuthService.changeAddress(
        userid: widget.userid,
        addressRoad: target.addressRoad, // 화면 텍스트가 프리뷰라도 서버는 그대로 받아도 무해
        addressJibun: target.addressJibun,
        postCode: target.postCode,
        addressDetail: target.addressDetail,
      );
      logi(
          '🌐 주소 변경 응답: status=${response.statusCode} body=${response.body}',
      );

      if (response.statusCode < 200 || response.statusCode >= 300) {
        throw Exception('서버 응답 코드 ${response.statusCode}');
      }

      // (로컬) 프리뷰+세부 필드 동시 갱신
      await UserStorage.patchAddressOnly(
        addressRoad: target.addressRoad,
        addressJibun: target.addressJibun,
        postCode: target.postCode,
        addressDetail: target.addressDetail,
      );

      setState(() {
        _current = target;
        _loading = false;
      });

      logi('✅ 주소 변경 완료: current=${_current?.toJson()}');

      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(const SnackBar(content: Text('배송 주소가 변경되었습니다.')));
      }
    } catch (e, st) {
      setState(() => _loading = false);
      logi('❌ 주소 변경 실패', error: e, stackTrace: st);
      if (mounted) {
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text('주소 변경 실패: $e')));
      }
    }
  }

  Widget _addressCard(AddressItem item,
      {Widget? trailing, VoidCallback? onTap}) {
    return InkWell(
      onTap: onTap,
      child: Container(
        width: double.infinity,
        constraints: const BoxConstraints(minHeight: 120),
        padding: const EdgeInsets.fromLTRB(16, 16, 16, 28),
        margin: const EdgeInsets.symmetric(vertical: 6),
        decoration: BoxDecoration(
          color: Colors.white,
          border: Border.all(color: const Color(0xFFCCCCCC)),
          borderRadius: BorderRadius.circular(8),
          boxShadow: const [
            BoxShadow(
                color: Color(0x14000000), blurRadius: 6, offset: Offset(0, 2)),
          ],
        ),
        child: Stack(
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  item.addressRoad,
                  softWrap: true,
                  maxLines: null,
                  style: const TextStyle(
                      fontSize: 16, fontWeight: FontWeight.w600),
                ),
                if (item.addressDetail != null &&
                    item.addressDetail!.isNotEmpty)
                  Padding(
                    padding: const EdgeInsets.only(top: 4),
                    child: Text(item.addressDetail!,
                        softWrap: true,
                        maxLines: null,
                        style: const TextStyle(fontSize: 15)),
                  ),
              ],
            ),
            if (trailing != null) Positioned(top: 0, right: 0, child: trailing),
            if (item.postCode != null && item.postCode!.isNotEmpty)
              Positioned(
                  right: 0,
                  bottom: 0,
                  child: _postcodeChips(item.postCode!, small: true)),
          ],
        ),
      ),
    );
  }

  Widget _postcodeChips(String postCode, {bool small = false}) {
    // 한 칸씩 박스
    final digits = postCode.split('');
    final EdgeInsets pad = small
        ? const EdgeInsets.symmetric(horizontal: 6, vertical: 3)
        : const EdgeInsets.symmetric(horizontal: 8, vertical: 6);
    final double fontSize = small ? 12 : 14;
    final double gap = small ? 3 : 4;

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: digits.map((d) {
        return Container(
          margin: EdgeInsets.only(right: gap),
          padding: pad,
          decoration: BoxDecoration(
            border: Border.all(color: Colors.black87),
          ),
          child: Text(d, style: TextStyle(fontSize: fontSize)),
        );
      }).toList(),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('배송 주소 변경'),
        centerTitle: true,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.pop(context),
        ),
        elevation: 0.5,
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _bootstrap,
              child: SingleChildScrollView(
                physics: const AlwaysScrollableScrollPhysics(),
                padding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 12,
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Divider(),
                    const SizedBox(height: 12),
                    const Text(
                      '현재 배송 주소',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 8),
                    if (_current != null)
                      _addressCard(_current!)
                    else
                      Container(
                        padding: const EdgeInsets.all(16),
                        decoration: BoxDecoration(
                          color: Colors.white,
                          border: Border.all(color: const Color(0xFFCCCCCC)),
                          borderRadius: BorderRadius.circular(8),
                          boxShadow: const [
                            BoxShadow(
                              blurRadius: 6,
                              offset: Offset(0, 2),
                              color: Color(0x14000000),
                            ),
                          ],
                        ),
                        child: const Text('현재 주소가 없습니다.'),
                      ),
                    const SizedBox(height: 16),
                    const Divider(),
                    const SizedBox(height: 8),
                    const Text(
                      '배송 주소 목록',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 8),
                    if (_address.isEmpty)
                      const Padding(
                        padding: EdgeInsets.symmetric(vertical: 12),
                        child: Text('저장된 주소가 없습니다. 아래 버튼으로 추가하세요.'),
                      ),
                    ...List.generate(_address.length, (i) {
                      final item = _address[i];
                      return _addressCard(
                        item,
                        trailing: Radio<int?>(
                          value: i,
                          groupValue: _selectedIndex,
                          onChanged: (v) => setState(() => _selectedIndex = v),
                        ),
                        onTap: () => setState(() => _selectedIndex = i),
                      );
                    }),

                    const SizedBox(height: 8),

                    // 배송 주소 추가 버튼
                    Center(
                      child: SizedBox(
                        width: 250,
                        child: ElevatedButton(
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color.fromARGB(
                              255,
                              197,
                              214,
                              88,
                            ),
                            foregroundColor: Colors.black,
                            padding: const EdgeInsets.symmetric(vertical: 14),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(24),
                            ),
                          ),
                          onPressed: _addAddressViaKpostal,
                          child: const Text(
                            '배송 주소 추가',
                            style: TextStyle(
                              fontSize: 18,
                              color: Colors.black,
                            ),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(height: 20),

                    // 선택한 주소로 변경 버튼
                    Center(
                      child: SizedBox(
                        width: 250,
                        child: FilledButton(
                          style: FilledButton.styleFrom(
                            backgroundColor: const Color.fromARGB(
                              255,
                              235,
                              239,
                              165,
                            ),
                          ),
                          onPressed: _changeToSelected,
                          child: const Padding(
                            padding: EdgeInsets.symmetric(vertical: 14),
                            child: Text(
                              '선택한 주소로 변경',
                              style: TextStyle(
                                fontSize: 18,
                                color: Colors.black,
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
    );
  }
}

/// 주소 아이템 도메인
class AddressItem {
  final String addressRoad;
  final String? addressJibun;
  final String? postCode;
  final String? addressDetail;

  AddressItem({
    required this.addressRoad,
    this.addressJibun,
    this.postCode,
    this.addressDetail,
  });

  String get preview {
    if (addressDetail != null && addressDetail!.isNotEmpty) {
      return '$addressRoad $addressDetail';
    }
    return addressRoad;
  }

  Map<String, dynamic> toJson() => {
        'addressRoad': addressRoad,
        if (addressJibun != null) 'addressJibun': addressJibun,
        if (postCode != null) 'postCode': postCode,
        if (addressDetail != null) 'addressDetail': addressDetail,
      };

  factory AddressItem.fromJson(Map<String, dynamic> map) => AddressItem(
        addressRoad: map['addressRoad'] as String? ?? '',
        addressJibun: map['addressJibun'] as String?,
        postCode: map['postCode'] as String?,
        addressDetail: map['addressDetail'] as String?,
      );

  bool sameAs(AddressItem other) {
    final rdEq = addressRoad.trim() == other.addressRoad.trim();
    final detEq =
        (addressDetail ?? '').trim() == (other.addressDetail ?? '').trim();
    final pcEq = (postCode ?? '').trim() == (other.postCode ?? '').trim();

    // 우편번호가 둘 다 없으면 도로명(+상세)만으로 동일 판단
    if ((postCode == null || postCode!.isEmpty) &&
        (other.postCode == null || other.postCode!.isEmpty)) {
      return rdEq && detEq;
    }
    return rdEq && detEq && pcEq;
  }
}
