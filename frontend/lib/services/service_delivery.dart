// service_delivery.dart

/*
배송 요청 전송
[userId] : 사용자 ID
[requestPayload] : { "yyyy-MM-dd": ["breakfast","lunch", ...], ... }
*/

import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:frontend/services/api_constants.dart';
import 'package:frontend/models/daily_meals.dart';

class DeliveryService {

  // 배송 요청 전송 API 
  static Future<bool> request(
    String userId,
    Map<String, List<String>> requestPayload, 
  ) async {
    final uri = Uri.parse('${ApiConstants.baseUrl}/api/delivery/requests');

    final body = jsonEncode({
      "userId": userId,
      "requests": requestPayload,
    });

    final headers = {
      "Content-Type": "application/json; charset=UTF-8",
    };

    try {
      debugPrint('POST ${uri.toString()}');
      debugPrint('BODY $body');

      final res = await http.post(uri, headers: headers, body: body);
      debugPrint('STATUS ${res.statusCode} BODY ${res.body}');

      if (res.statusCode >= 200 && res.statusCode < 300) {
        return true;
      } else {
        debugPrint('Delivery request failed: ${res.statusCode} ${res.body}');
        return false;
      }
    } catch (e) {
      debugPrint('Delivery request error: $e');
      return false;
    }
  }

  // 배송 중인 식단 조회 API
  static Future<Map<String, DailyMeals>> fetchInTransitMeals(String userid) async {
    final uri = Uri.parse('${ApiConstants.baseUrl}/api/delivery/preparing/$userid');
    final resp = await http.get(uri, headers: {'Content-Type': 'application/json'});

    if (resp.statusCode != 200) {
      throw Exception('배송 중인 식단 조회 실패: ${resp.statusCode} ${resp.body}');
    }

    final Map<String, dynamic> raw = jsonDecode(resp.body);
    return raw.map((date, v) => MapEntry(date, DailyMeals.fromJson(v)));
  }
}