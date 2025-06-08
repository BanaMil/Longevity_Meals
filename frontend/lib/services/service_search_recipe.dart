// service_search_recipe.dart

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:frontend/services/api_constants.dart';

class SearchRecipeService {
  static Future<List<String>> search(String keyword) async {
    final url = Uri.parse(
      '${ApiConstants.baseUrl}/api/recipes/search?query=$keyword',
    ); //백엔드랑 맞추기!

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        return data.map((e) => e.toString()).toList();
      } else {
        throw Exception('서버 오류: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('요청 실패: $e');
    }
  }
}
