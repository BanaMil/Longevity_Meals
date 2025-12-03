// service_search_recipe.dart

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:frontend/services/api_constants.dart';
import 'package:frontend/models/food_item.dart';

class SearchRecipeService {
  static Future<List<FoodItem>> search(String keyword) async {
    final url = '${ApiConstants.baseUrl}/api/foods/search?query=$keyword';

    final response = await http.get(
      Uri.parse(url), 
      headers: const {'Accept': 'application/json'},
    );

    if (response.statusCode != 200) {
      throw Exception('서버 오류: ${response.statusCode}');
    }

    final body = utf8.decode(response.bodyBytes).trim();
    if (body.isEmpty) return const [];

    final decoded = json.decode(body);

    if (decoded is List) {
      return decoded
          .map((e) => FoodItem.fromJson(e as Map<String, dynamic>))
          .toList();
    }

    if (decoded is Map<String, dynamic>) {
      return [FoodItem.fromJson(decoded)];
    }

    throw Exception('예상치 못한 응답 형식입니다.');
  }
}
