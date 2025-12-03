// service_health_info.dart

import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:frontend/models/health_info.dart';
import 'package:frontend/services/api_constants.dart';
import 'package:frontend/services/user_storage.dart';

class HealthInfoService {
  static Future<void> uploadHealthInfo(HealthInfo info) async {
    final token = await UserStorage.loadToken();

    if (token == null) {
      throw Exception("❌ JWT 토큰이 저장되어 있지 않습니다. 로그인을 먼저 수행해야 합니다.");
    }
    final url = Uri.parse('${ApiConstants.baseUrl}/api/health/health_info');

    // 전송할 JSON 문자열
    final jsonBody = jsonEncode(info.toJson());

    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token', // JWT 인증 헤더
      },
      body: jsonBody,
    );

    debugPrint('📥 응답 상태 코드: ${response.statusCode}');
    debugPrint('📥 응답 본문: ${response.body}');

    if (response.statusCode != 200) {
      final body = jsonDecode(response.body);
      final msg = body['message'] ?? '서버 오류';
      throw Exception("건강정보 저장 실패: $msg");
    }
  }
}
