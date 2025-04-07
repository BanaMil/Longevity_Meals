import 'dart:convert';
import 'package:http/http.dart' as http; 

class ApiService {
  static const String _baseUrl = 'http://10.0.2.2:8080';

  // 회원가입용 API
  static Future<http.Response> registerUser({
    required String username, 
    required String id,
    required String password,
    required String birthdate,
  }) async {
    final url = Uri.parse('$_baseUrl/api/signup');
    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'username': username,
        'id': id,
        'password': password,
        'birthdate': birthdate,
      }),
    );
    return response;
  }

  // 로그인용 API
  static Future<http.Response> loginUser({
    required String id,
    required String password,
  }) async {
    final url = Uri.parse('$_baseUrl/api/login'); // Spring Boot 로그인 엔드포인트
    final response = await http.post(
      url,
      headers: {"Content-Type": "application/json"},
      body: jsonEncode({
        "id": id,
        "password": password,
      }),
    );
    return response;
  }

  // 아이디 중복확인 API
  static Future<http.Response> checkIdDuplicate(String id) async {
    final url = Uri.parse('$_baseUrl/api/check-id');
    final response = await http.get(
      url.replace(queryParameters: {'id': id}),
      headers: {"Content-Type": "application/json"},
    );
    return response;  
  }
}


