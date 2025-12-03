// service_health_result.dart

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:frontend/services/api_constants.dart';
import 'package:frontend/models/health_result.dart';

class HealthResultService {
  static Future<HealthResult> fetchAnalysisResult(String userid) async {
    final url = Uri.parse(
      '${ApiConstants.baseUrl}/api/health/analysis/$userid',
    ); 
    final response = await http.get(url);

    if (response.statusCode == 200) {
      if (response.body.isNotEmpty) {
        final data = json.decode(utf8.decode(response.bodyBytes));
        return HealthResult.fromJson(data);
      } else {
        throw Exception("서버 응답이 비어 있습니다.");
      }
    } else {
      throw Exception("분석 결과 불러오기 실패: ${response.statusCode}");
    }
  }
}
