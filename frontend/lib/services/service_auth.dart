// service_auth.dart

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter/foundation.dart';
import 'package:frontend/models/user.dart';
import 'package:frontend/services/api_constants.dart';
import 'package:frontend/services/user_storage.dart';
import 'package:frontend/models/address_item.dart';

class AuthService {
  // 회원가입 API
  static Future<http.Response> registerUser({
    required String username,
    required String userid,
    required String password,
    required String birthdate,
    required String phone,
    required String address,
    String? addressRoad,
    String? addressJibun,
    String? postCode,
    String? addressDetail,
  }) {
    final body = {
      'username': username,
      'userid': userid,
      'password': password,
      'birthdate': birthdate,
      'phone': phone,
      'address': address,                         
      'addressRoad': addressRoad ?? address,      
      if (addressJibun != null) 'addressJibun': addressJibun,
      if (postCode != null) 'postCode': postCode,
      if (addressDetail != null) 'addressDetail': addressDetail,
    };

    return http.post(
      Uri.parse('${ApiConstants.baseUrl}/api/auth/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(body),
    );
  }


  // 로그인 API
static Future<User> loginUser({
  required String userid,
  required String password,
}) async {
  debugPrint("🔐 Trying login with ID: '$userid'");

  final res = await http.post(
    Uri.parse('${ApiConstants.baseUrl}/api/auth/login'),
    headers: {'Content-Type': 'application/json'},
    body: jsonEncode({'userid': userid, 'password': password}),
  );

  if (res.statusCode != 200) {
    throw Exception('로그인 실패: ${res.statusCode} ${res.body}');
  }

  final Map<String, dynamic> top = jsonDecode(res.body) as Map<String, dynamic>;
  final data = top['data'];
  if (data is! Map<String, dynamic>) {
    throw Exception('로그인 응답 형식 오류: data가 없습니다.');
  }

  final String useridResp        = (data['userid'] ?? '') as String;
  final String usernameResp      = (data['username'] ?? '') as String;
  final String? addressResp      = data['address'] as String?;
  final String? addressJibunResp = data['addressJibun'] as String?;
  final String? addressRoadResp  = data['addressRoad'] as String?;
  final String? postCodeResp     = data['postCode'] as String?;
  final String? addressDetailResp= data['addressDetail'] as String?;
  final String? tokenResp        = data['token'] as String?;
  final bool healthInfoSubmitted = (data['healthInfoSubmitted'] as bool?) ?? false;

  final List<AddressItem> addresses = (data['addresses'] as List<dynamic>? ?? const [])
      .whereType<Map<String, dynamic>>()
      .map(AddressItem.fromJson)
      .toList();

  // 로컬 저장
  await UserStorage.saveUserInfo(
    username: usernameResp,
    userid: useridResp,
    address: addressResp,
    addressRoad: addressRoadResp,
    addressJibun: addressJibunResp,
    postCode: postCodeResp,
    addressDetail: addressDetailResp,
    healthInfoSubmitted: healthInfoSubmitted,
  );
  if (tokenResp != null && tokenResp.isNotEmpty) {
    await UserStorage.saveToken(tokenResp);
  }
  if (addresses.isNotEmpty) {
    await UserStorage.saveAddressList(
      useridResp,
      addresses.map((a) => a.toJson()).toList(),
    );
  }

  // 호출부 호환: User 객체 조립
  return User(
    userid: useridResp,
    username: usernameResp,
    address: addressResp,
    addressRoad: addressRoadResp,
    addressJibun: addressJibunResp,
    postCode: postCodeResp,
    addressDetail: addressDetailResp,
    token: tokenResp,
    healthInfoSubmitted: healthInfoSubmitted,
    addresses: addresses,
  );
}

  // 아이디 중복확인
  static Future<http.Response> checkIdDuplicate(String userid) async {
    final url = Uri.parse('${ApiConstants.baseUrl}/api/auth/check-id');
    return http.get(
      url.replace(queryParameters: {'userid': userid}),
      headers: {'Content-Type': 'application/json'},
    );
  }

  // 비밀번호 변경
  static Future<http.Response> changePassword({
    required String userid,
    required String currentPassword,
    required String newPassword,
  }) {
    final url = Uri.parse('${ApiConstants.baseUrl}/api/auth/change-password');
    return http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'userid': userid,
        'currentPassword': currentPassword,
        'newPassword': newPassword,
      }),
    );
  }

  // 주소 변경
  static Future<http.Response> changeAddress({
    required String userid,
    required String addressRoad,
    String? addressJibun,
    String? postCode,
    String? addressDetail,
  }) async {
    final token = await UserStorage.loadToken();
    final url = Uri.parse(
      '${ApiConstants.baseUrl}/api/auth/users/$userid/addresses/current',
    );

    final body = {
      'addressRoad': addressRoad,
      if (addressJibun != null) 'addressJibun': addressJibun,
      if (postCode != null) 'postCode': postCode,
      if (addressDetail != null) 'addressDetail': addressDetail,
    };

    return http.put(
      url,
      headers: {
        'Content-Type': 'application/json',
        if (token != null) 'Authorization': 'Bearer $token',
      },
      body: jsonEncode(body),
    );
  }

  // 주소 추가
  static Future<http.Response> addAddress({
    required String userid,
    required String addressRoad,
    String? addressJibun,
    String? postCode,
    String? addressDetail,
  }) {
    final url = Uri.parse(
      '${ApiConstants.baseUrl}/api/auth/users/$userid/addresses',
    );

    final body = {
      'addressRoad': addressRoad,
      if (addressJibun != null) 'addressJibun': addressJibun,
      if (postCode != null) 'postCode': postCode,
      if (addressDetail != null) 'addressDetail': addressDetail,
    };

    return http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(body),
    );
  }
}
