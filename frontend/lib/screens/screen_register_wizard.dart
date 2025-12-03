// screen_register_wizard.dart

import 'package:flutter/material.dart';
import 'package:kpostal/kpostal.dart';
import 'package:provider/provider.dart';

import 'package:frontend/utils/validators.dart';
import 'package:frontend/services/user_storage.dart';
import 'package:frontend/screens/screen_login.dart';

import 'package:frontend/widgets/register_progress_bar.dart';
import 'package:frontend/providers/register_provider.dart';

class RegisterWizardScreen extends StatefulWidget {
  const RegisterWizardScreen({super.key});
  @override
  State<RegisterWizardScreen> createState() => _RegisterWizardScreenState();
}

class _RegisterWizardScreenState extends State<RegisterWizardScreen> {
  final _page = PageController();

  // 각 스텝 폼키
  final _form0 = GlobalKey<FormState>();
  final _form1 = GlobalKey<FormState>();
  final _form2 = GlobalKey<FormState>();
  final _form3 = GlobalKey<FormState>();
  final _form4 = GlobalKey<FormState>();

  Future<void> _pickBirthdate(BuildContext context) async {
    final p = context.read<RegisterProvider>();
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: DateTime(1970),
      firstDate: DateTime(1900),
      lastDate: DateTime(now.year, now.month, now.day),
      locale: const Locale('ko', 'KR'),
    );
    if (picked != null) {
      p.setBirthdate(picked);
    }
  }

  Future<void> _searchAddress(BuildContext context) async {
    final result = await Navigator.push<Kpostal>(
      context,
      MaterialPageRoute(builder: (_) => KpostalView()),
    );
    if (!mounted || result == null) return;

    final p = context.read<RegisterProvider>();

    final roadOrAddr =
        (result.roadAddress.isNotEmpty ? result.roadAddress : result.address)
            .trim();
    final jibun = result.jibunAddress.trim();
    final post = result.postCode.trim();

    p.setAddressRoad(roadOrAddr);
    p.setAddressJibun(jibun.isEmpty ? null : jibun);
    p.setPostCode(post.isEmpty ? null : post);
  }

  void _goNext(BuildContext context) {
    final p = context.read<RegisterProvider>();

    if (!p.validateStep(p.step)) return;

    if (p.step < p.totalSteps - 1) {
      p.next();
      _page.animateToPage(p.step,
          duration: const Duration(milliseconds: 250), curve: Curves.easeOut);
    }
  }

  void _goPrev(BuildContext context) {
    final p = context.read<RegisterProvider>();
    if (p.step == 0) return;
    p.prev();
    _page.animateToPage(p.step,
        duration: const Duration(milliseconds: 200), curve: Curves.easeOut);
  }

  Future<void> _submitAll(BuildContext context) async {
    final p = context.read<RegisterProvider>();
    final ok = await p.submit();
    if (!mounted) return;

    if (ok) {
      // SharedPreferences 저장
      await UserStorage.saveUserInfo(
        username: p.username!,
        userid: p.userid!,
        address: [
          if (p.addressRoad?.isNotEmpty == true) p.addressRoad,
          if (p.addressDetail?.isNotEmpty == true) p.addressDetail
        ].join(' '),
        addressRoad: p.addressRoad,
        addressJibun: p.addressJibun,
        postCode: p.postCode,
        addressDetail: p.addressDetail,
      );
      Navigator.pushReplacement(
          context, MaterialPageRoute(builder: (_) => const LoginScreen()));
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('입력값을 확인하거나, 잠시 후 다시 시도해주세요.')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final p = context.watch<RegisterProvider>();
    final w = MediaQuery.of(context).size.width * 0.85;

    Widget bottomBar(
        {required bool showPrev,
        required bool showNext,
        required bool showSubmit}) {
      return Row(
        children: [
          if (showPrev)
            TextButton(
                onPressed: () => _goPrev(context), child: const Text('이전')),
          const Spacer(),
          if (showNext)
            FilledButton(
              onPressed: () => _goNext(context),
              child: const Text('다음'),
            ),
          if (showSubmit)
            FilledButton(
              onPressed: p.isSubmitting ? null : () => _submitAll(context),
              child: p.isSubmitting
                  ? const SizedBox(
                      width: 18,
                      height: 18,
                      child: CircularProgressIndicator(strokeWidth: 2))
                  : const Text('회원가입 완료'),
            ),
        ],
      );
    }

    final stepTitle = [
      '이름과 생년월일을 입력해주세요.',
      '아이디를 입력하고 중복 확인을 해주세요.',
      '비밀번호를 입력해주세요.',
      '휴대폰 번호를 입력해주세요.',
      '주소를 입력해주세요.',
    ][p.step];

    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 251, 255, 228),
      appBar: AppBar(
        title:
            const Text('회원가입', style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: const Color.fromARGB(255, 251, 255, 228),
        automaticallyImplyLeading: p.step == 0, 
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(18),
          child: Column(
            children: [
              RegisterProgressBar(step: p.step, total: p.totalSteps),
              const SizedBox(height: 16),
              Align(
                  alignment: Alignment.centerLeft,
                  child: Text(stepTitle,
                      style: const TextStyle(
                          fontSize: 18, fontWeight: FontWeight.w600))),
              const SizedBox(height: 12),
              Expanded(
                child: PageView(
                  controller: _page,
                  physics: const NeverScrollableScrollPhysics(),
                  children: [
                    // STEP 1: 이름/생년월일
                    Form(
                      key: _form0,
                      child: ListView(
                        children: [
                          SizedBox(
                            width: w,
                            child: TextFormField(
                              decoration:
                                  const InputDecoration(labelText: '이름'),
                              initialValue: p.username,
                              validator: Validators.validateUsername,
                              onChanged: (v) => p.setUsername(v),
                            ),
                          ),
                          const SizedBox(height: 12),
                          InkWell(
                            onTap: () => _pickBirthdate(context),
                            child: InputDecorator(
                              decoration: const InputDecoration(
                                labelText: '생년월일',
                                border: OutlineInputBorder(),
                              ),
                              child: Text(
                                p.birthdate == null
                                    ? '생년월일을 선택해주세요'
                                    : '${p.birthdate!.year}-${p.birthdate!.month.toString().padLeft(2, '0')}-${p.birthdate!.day.toString().padLeft(2, '0')}',
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),

                    // STEP 2: 아이디 + 중복확인
                    Form(
                      key: _form1,
                      child: ListView(
                        children: [
                          SizedBox(
                            width: w,
                            child: TextFormField(
                              decoration:
                                  const InputDecoration(labelText: '아이디'),
                              initialValue: p.userid,
                              validator: Validators.validateId,
                              onChanged: (v) => p.setUserid(v),
                            ),
                          ),
                          const SizedBox(height: 8),
                          OutlinedButton(
                            onPressed: () async {
                              final ok = await p.checkIdDuplicate();
                              if (!mounted) return;
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(
                                    content: Text(ok
                                        ? '사용 가능한 아이디입니다.'
                                        : '이미 존재하는 아이디입니다.')),
                              );
                            },
                            child: const Text('아이디 중복 확인'),
                          ),
                          const SizedBox(height: 6),
                          if (p.idAvailable != null)
                            Text(
                              p.idAvailable! ? '사용 가능' : '사용 불가',
                              style: TextStyle(
                                  color: p.idAvailable!
                                      ? Colors.green
                                      : Colors.red),
                            ),
                        ],
                      ),
                    ),

                    // STEP 3: 비밀번호
                    Form(
                      key: _form2,
                      child: ListView(
                        children: [
                          TextFormField(
                            decoration:
                                const InputDecoration(labelText: '비밀번호'),
                            obscureText: true,
                            validator: Validators.validatePassword,
                            onChanged: (v) => p.password = v,
                          ),
                          const SizedBox(height: 12),
                          TextFormField(
                            decoration:
                                const InputDecoration(labelText: '비밀번호 확인'),
                            obscureText: true,
                            onChanged: (v) => p.setConfirmPassword(v),
                          ),
                          const SizedBox(height: 6),
                          if (p.confirmPassword != null &&
                              p.confirmPassword!.isNotEmpty)
                            Text(
                              p.confirmPassword == p.password
                                  ? '일치합니다.'
                                  : '비밀번호가 일치하지 않습니다.',
                              style: TextStyle(
                                  color: p.confirmPassword == p.password
                                      ? Colors.green
                                      : Colors.red),
                            ),
                        ],
                      ),
                    ),

                    // STEP 4: 휴대폰
                    Form(
                      key: _form3,
                      child: ListView(
                        children: [
                          TextFormField(
                            decoration:
                                const InputDecoration(labelText: '휴대폰 번호'),
                            keyboardType: TextInputType.phone,
                            validator: (v) => (v == null || v.trim().isEmpty)
                                ? '휴대폰 번호를 입력해주세요.'
                                : null,
                            onChanged: (v) => p.phone = v,
                          ),
                        ],
                      ),
                    ),

                    // STEP 5: 주소
                    Form(
                      key: _form4,
                      autovalidateMode: AutovalidateMode.onUserInteraction,
                      child: ListView(
                        children: [
                          SizedBox(height: 10),
                          FormField<String>(
                            validator: (_) =>
                                (p.addressRoad?.isNotEmpty == true)
                                    ? null
                                    : '주소를 선택해주세요.',
                            builder: (field) => InkWell(
                              onTap: () => _searchAddress(context),
                              child: InputDecorator(
                                decoration: InputDecoration(
                                  labelText: '주소 검색 (도로명/지번/우편번호)',
                                  border: const OutlineInputBorder(),
                                  errorText: field.errorText,
                                ),
                                child: Text(
                                  p.addressRoad?.isNotEmpty == true
                                      ? p.addressRoad!
                                      : '주소를 검색하세요.',
                                ),
                              ),
                            ),
                          ),
                          const SizedBox(height: 12),
                          TextFormField(
                            decoration: const InputDecoration(
                              labelText: '상세 주소 (선택)',
                              border: OutlineInputBorder(),
                            ),
                            onChanged: (v) => p.setAddressDetail(v),
                          ),
                          const SizedBox(height: 12),
                          const Text('입력된 주소',
                              style: TextStyle(fontWeight: FontWeight.w600)),
                          const SizedBox(height: 6),
                          Container(
                            padding: const EdgeInsets.all(12),
                            decoration: BoxDecoration(
                              color: Colors.white,
                              border: Border.all(color: Colors.black26),
                              borderRadius: BorderRadius.circular(8),
                            ),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                if (p.postCode?.isNotEmpty == true)
                                  Text('우편번호: ${p.postCode}'),
                                if (p.addressRoad?.isNotEmpty == true)
                                  Text('도로명 주소: ${p.addressRoad}'),
                                if (p.addressDetail?.isNotEmpty == true)
                                  Text('상세 주소: ${p.addressDetail}'),
                                if (p.addressJibun?.isNotEmpty == true)
                                  Text('지번 주소: ${p.addressJibun}'),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 8),
              bottomBar(
                showPrev: p.step > 0,
                showNext: p.step < 4,
                showSubmit: p.step == 4,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
