// meal.dart

import 'food_item.dart';

class Meal {
  final FoodItem rice;
<<<<<<< HEAD
  final FoodItem? soup;  
=======
  final FoodItem? soup;   // nullable 
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
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

