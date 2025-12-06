// register_provider.dart

import 'package:flutter/foundation.dart';
import 'package:frontend/services/service_auth.dart';

class RegisterProvider extends ChangeNotifier {
  // 0..4
  int _step = 0;
  int get step => _step;
  int get totalSteps => 5;

  String? username;
  DateTime? birthdate;

  String? userid;
  bool? idAvailable; // null=미확인, true=가능, false=불가

  String? password;
  String? confirmPassword;

  String? phone;

  // 주소
  String? addressRoad; // 대표(필수)
  String? addressJibun; // 선택
  String? postCode; // 선택
  String? addressDetail; // 선택

  bool isSubmitting = false;

  void setUsername(String v) {
    username = v;
    notifyListeners();
  }

  void setBirthdate(DateTime d) {
    birthdate = d;
    notifyListeners();
  }

  void setUserid(String v) {
    userid = v;
    idAvailable = null;
    notifyListeners();
  }

  void setPassword(String v) {
    password = v;
    notifyListeners();
  }

  void setConfirmPassword(String v) {
    confirmPassword = v;
    notifyListeners();
  }

  void setPhone(String v) {
    phone = v;
    notifyListeners();
  }

  void setAddressRoad(String v) {
    addressRoad = v;
    notifyListeners();
  }

  void setAddressDetail(String v) {
    addressDetail = v;
    notifyListeners();
  }

  void setAddressJibun(String? v) {
    addressJibun = v;
    notifyListeners();
  }

  void setPostCode(String? v) {
    postCode = v;
    notifyListeners();
  }

  // 진행 제어
  void next() {
    if (_step < totalSteps - 1) {
      _step++;
      notifyListeners();
    }
  }

  void prev() {
    if (_step > 0) {
      _step--;
      notifyListeners();
    }
  }

  // 유효성
  bool validateStep(int s) {
    switch (s) {
      case 0:
        return (username != null && username!.trim().isNotEmpty) &&
            (birthdate != null);
      case 1:
        return (userid != null && userid!.trim().isNotEmpty) &&
            (idAvailable == true);
      case 2:
        return (password != null && password!.length >= 8) &&
            (confirmPassword == password);
      case 3:
        return (phone != null && phone!.trim().isNotEmpty);
      case 4:
        return (addressRoad != null && addressRoad!.trim().isNotEmpty);
      default:
        return false;
    }
  }

  // 아이디 중복확인
  Future<bool> checkIdDuplicate() async {
    if (userid == null || userid!.trim().isEmpty) return false;
    final res = await AuthService.checkIdDuplicate(userid!.trim());
    final ok = res.statusCode == 200 &&
        (res.body.contains('"available":true') ||
            res.body.contains('available": true'));
    idAvailable = ok;
    notifyListeners();
    return ok;
  }

  // 최종 전송
  Future<bool> submit() async {
    if (isSubmitting) return false;
    if (!(validateStep(0) &&
        validateStep(1) &&
        validateStep(2) &&
        validateStep(3) &&
        validateStep(4))) {
      return false;
    }
    isSubmitting = true;
    notifyListeners();

    try {
      final birth = birthdate!;
      final birthStr =
          '${birth.year.toString().padLeft(4, '0')}-${birth.month.toString().padLeft(2, '0')}-${birth.day.toString().padLeft(2, '0')}';

      final fullPreview = [
        if (addressRoad?.isNotEmpty == true) addressRoad,
        if (addressDetail?.isNotEmpty == true) addressDetail,
      ].join(' ');

      final res = await AuthService.registerUser(
        username: username!.trim(),
        userid: userid!.trim(),
        password: password!.trim(),
        birthdate: birthStr,
        phone: phone!.trim(),
        address: fullPreview,
        addressRoad: addressRoad,
        addressJibun: addressJibun,
        postCode: postCode,
        addressDetail: addressDetail,
      );
      return res.statusCode == 200;
    } finally {
      isSubmitting = false;
      notifyListeners();
    }
  }

  void reset() {
    _step = 0;

    username = null;
    birthdate = null;

    userid = null;
    idAvailable = null;

    password = null;
    confirmPassword = null;

    phone = null;

    addressRoad = null;
    addressJibun = null;
    postCode = null;
    addressDetail = null;

    isSubmitting = false;

    notifyListeners();
  }
}
