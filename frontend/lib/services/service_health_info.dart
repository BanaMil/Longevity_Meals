// service_health_info.dart

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:frontend/services/api_constants.dart';
import 'package:frontend/models/health_info.dart';

class HealthInfoService {
  static Future<void> uploadHealthInfo(HealthInfo info) async {
    final url = Uri.parse('${ApiConstants.baseUrl}/api/health/health_info');
    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(info.toJson()),
    );

    if(response.statusCode != 200) {
      throw Exception("서버 전송 실패");
    }
  }
}
