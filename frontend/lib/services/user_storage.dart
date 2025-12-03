import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

class UserStorage {
  // 프리뷰 주소(도로명 + 상세주소) 합치기 유틸
  static String buildPreviewAddress({
    String? addressRoad,
    String? addressDetail,
  }) {
    final parts = <String>[];
    if (addressRoad != null && addressRoad.trim().isNotEmpty) {
      parts.add(addressRoad.trim());
    }
    if (addressDetail != null && addressDetail.trim().isNotEmpty) {
      parts.add(addressDetail.trim());
    }
    return parts.join(' ');
  }

  // 회원가입 후 사용자 정보 저장
  static Future<void> saveUserInfo({
    required String username,
    required String userid,
    String? address,
    String? addressRoad,
    String? addressJibun,
    String? postCode,
    String? addressDetail,
    bool? healthInfoSubmitted,
  }) async {
    final prefs = await SharedPreferences.getInstance();

    final preview = (address != null && address.trim().isNotEmpty)
        ? address.trim()
        : buildPreviewAddress(
            addressRoad: addressRoad,
            addressDetail: addressDetail,
          );

    await prefs.setString('username', username);
    await prefs.setString('userid', userid);
    await prefs.setString('address', preview);

    if (addressRoad != null) await prefs.setString('addressRoad', addressRoad);
    if (addressJibun != null) await prefs.setString('addressJibun', addressJibun);
    if (postCode != null) await prefs.setString('postCode', postCode);
    if (addressDetail != null) await prefs.setString('addressDetail', addressDetail);

    if (healthInfoSubmitted != null) {
      await prefs.setBool('healthInfoSubmitted', healthInfoSubmitted);
    }
  }

  // 현재 주소만 패치(배송 주소 변경 시 사용)
  static Future<void> patchAddressOnly({
    required String addressRoad,
    String? addressJibun,
    String? postCode,
    String? addressDetail,
  }) async {
    final prefs = await SharedPreferences.getInstance();

    final preview = buildPreviewAddress(
      addressRoad: addressRoad,
      addressDetail: addressDetail,
    );

    await prefs.setString('address', preview);
    await prefs.setString('addressRoad', addressRoad);

    if (addressJibun != null) {
      await prefs.setString('addressJibun', addressJibun);
    } else {
      await prefs.remove('addressJibun');
    }
    if (postCode != null) {
      await prefs.setString('postCode', postCode);
    } else {
      await prefs.remove('postCode');
    }
    if (addressDetail != null) {
      await prefs.setString('addressDetail', addressDetail);
    } else {
      await prefs.remove('addressDetail');
    }
  }

  static Future<void> saveCurrentAddressPreview(String address) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('address', address);
  }

  static Future<Map<String, String?>> loadUserInfo() async {
    final prefs = await SharedPreferences.getInstance();
    final data = {
      'username': prefs.getString('username'),
      'userid': prefs.getString('userid'),
      'address': prefs.getString('address'),
      'addressRoad': prefs.getString('addressRoad'),
      'addressJibun': prefs.getString('addressJibun'),
      'postCode': prefs.getString('postCode'),
      'addressDetail': prefs.getString('addressDetail'),
    };
    return data;
  }

  static Future<bool?> getHealthInfoSubmitted() async {
    final prefs = await SharedPreferences.getInstance();
    final value = prefs.getBool('healthInfoSubmitted');
    return value;
  }

  static Future<void> setHealthInfoSubmitted(bool value) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('healthInfoSubmitted', value);
  }

  static Future<void> clearUserInfo() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('username');
    await prefs.remove('userid');
    await prefs.remove('address');
    await prefs.remove('addressRoad');
    await prefs.remove('addressJibun');
    await prefs.remove('postCode');
    await prefs.remove('addressDetail');
    await prefs.remove('healthInfoSubmitted');
  }

  // --- SharedPreferences 확장 ---

  static Future<void> saveToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('auth_token', token);
  }

  static Future<String?> loadToken() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('auth_token');
    return token;
  }

  static String _addrListKey(String userid) => 'addresses_$userid';

  static Future<void> saveAddressList(
      String userid, List<Map<String, dynamic>> list) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_addrListKey(userid), jsonEncode(list));
  }

  static Future<List<Map<String, dynamic>>> loadAddressList(
      String userid) async {
    final prefs = await SharedPreferences.getInstance();
    final s = prefs.getString(_addrListKey(userid));
    if (s == null || s.isEmpty) {
      return [];
    }
    final raw = jsonDecode(s) as List<dynamic>;
    return raw.cast<Map<String, dynamic>>();
  }
}
