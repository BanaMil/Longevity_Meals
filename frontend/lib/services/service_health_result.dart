// service_health_result.dart

import 'dart:convert';
import 'package:http/http.dart' as http; 
import 'package:frontend/services/api_constants.dart';
import 'package:frontend/models/health_result.dart';

class HealthResultService {
  static Future<HealthResult> fetchAnalysisResult(String userid) async {
    final url = Uri.parse('${ApiConstants.baseUrl}/api/analysis/$userid'); //백엔드랑 맞추기!
    final response = await http.get(url);

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return HealthResult.fromJson(data);
    } else {
      throw Exception("분석 결과 불러오기 실패: ${response.statusCode}");
    }
  }
}

