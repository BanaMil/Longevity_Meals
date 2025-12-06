// service_health_info.dart

import 'dart:convert';
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

    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode(info.toJson()),
    );

    if (response.statusCode != 200) {
      final body = jsonDecode(response.body);
      final msg = body['message'] ?? '서버 오류';
      throw Exception("건강정보 저장 실패: $msg");
    }
  }
}
