// meal.dart

import 'food_item.dart';

class Meal {
  final FoodItem rice;
  final FoodItem? soup;   // nullable 
  final List<FoodItem> sideDishes;

  Meal({
    required this.rice,
    required this.soup,
    required this.sideDishes,
  });

  factory Meal.fromJson(Map<String, dynamic> json) {
    return Meal(
      rice: FoodItem.fromJson(json['rice']),
      soup: json['soup'] != null ? FoodItem.fromJson(json['soup']) : null,
      sideDishes: List<Map<String, dynamic>>.from(json['sideDishes'] ?? [])
          .map((e) => FoodItem.fromJson(e))
          .toList(),
    );
  }
}

