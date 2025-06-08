// service_meal.dart

import 'dart:developer';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:frontend/services/api_constants.dart';
import 'package:frontend/models/meal.dart';
import 'package:frontend/models/daily_meals.dart';

class MealService {
  // 오늘의 밥상(홈화면 및 음식 정보 보기)
  static Future<Meal> fetchTodayMeal(String userid) async {
    final url = Uri.parse('${ApiConstants.baseUrl}/api/meals/today');

    log('POST /api/meals/today 호출 시작');
    log('요청 본문: ${jsonEncode({"userid": userid})}');

    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({"userid": userid}),
    );

    if (response.statusCode == 200) {
      final decoded = jsonDecode(utf8.decode(response.bodyBytes));
      return Meal.fromJson(decoded);
    } else {
      log('서버 응답 코드: ${response.statusCode}');
      log('서버 응답 본문: ${response.body}');
      throw Exception('오늘의 밥상 로딩 실패: ${response.statusCode}');
    }
  }

  // 일주일 밥상 요청 API
  static Future<void> requestMealRecommendation(String userid) async {
    final url = Uri.parse('${ApiConstants.baseUrl}/api/meals/recommend');
    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({"userid": userid}),
    );

    if (response.statusCode != 200) {
      throw Exception("식단 추천 요청 실패: ${response.statusCode}");
    }
  }

  // 일주일 밥상
  static Future<Map<String, DailyMeals>> fetchWeeklyMeals(String userid) async {
    final url = Uri.parse('${ApiConstants.baseUrl}/api/meals/weekly/$userid');
    final response = await http.get(url);

    if (response.statusCode == 200) {
      final decoded = jsonDecode(utf8.decode(response.bodyBytes)) as Map<String, dynamic>;
      return decoded.map(
        (key, value) => MapEntry(key, DailyMeals.fromJson(value)),
      );
    } else {
      throw Exception('일주일 밥상 로딩 실패: ${response.statusCode}');
    }
  }
}
