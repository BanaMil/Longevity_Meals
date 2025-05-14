// service_auth.dart

import 'dart:convert';
import 'package:http/http.dart' as http; 
import 'package:frontend/services/api_constants.dart';

class AuthService {
  // 회원가입용 API
  static Future<http.Response> registerUser({
    required String username, 
    required String userid,
    required String password,
    required String birthdate,
    required String phone,
    required String address,
  }) async {
    final url = Uri.parse('${ApiConstants.baseUrl}/api/auth/register');
    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'username': username,
        'userid': userid,
        'password': password,
        'birthdate': birthdate,
        'phone': phone,
        'address': address,
      }),
    );
    return response;
  }

  // 로그인용 API
  static Future<http.Response> loginUser({
    required String userid,
    required String password,
  }) async {
    final url = Uri.parse('${ApiConstants.baseUrl}/api/auth/login'); // Spring Boot 로그인 엔드포인트
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode({
        "userid": userid,
        "password": password,
      }),
    );
    return response;
  }

  // 아이디 중복확인 API
  // GET 방식으로 보냈고, 예시 URL: /api/check-id?id=test123
  static Future<http.Response> checkIdDuplicate(String userid) async {
    final url = Uri.parse('${ApiConstants.baseUrl}/api/auth/check-id');
    final response = await http.get(
      url.replace(queryParameters: {'userid': userid}),
      headers: {"Content-Type": "application/json"},
    );
    return response;  
  }
}


