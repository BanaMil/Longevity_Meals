<<<<<<< HEAD
import 'package:frontend/models/food_with_intake.dart';

class DailyMeals {
  final List<FoodWithIntake> breakfast;
  final List<FoodWithIntake> lunch;
  final List<FoodWithIntake> dinner;
=======
// daily_meals.dart

class DailyMeals {
  final List<String> breakfast;
  final List<String> lunch;
  final List<String> dinner;
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db

  DailyMeals({
    required this.breakfast,
    required this.lunch,
    required this.dinner,
  });

  factory DailyMeals.fromJson(Map<String, dynamic> json) {
    return DailyMeals(
<<<<<<< HEAD
      breakfast: (json['breakfast'] as List<dynamic>)
          .map((item) => FoodWithIntake.fromJson(item))
          .toList(),
      lunch: (json['lunch'] as List<dynamic>)
          .map((item) => FoodWithIntake.fromJson(item))
          .toList(),
      dinner: (json['dinner'] as List<dynamic>)
          .map((item) => FoodWithIntake.fromJson(item))
          .toList(),
=======
      breakfast: List<String>.from(json['breakfast'] ?? []),
      lunch: List<String>.from(json['lunch'] ?? []),
      dinner: List<String>.from(json['dinner'] ?? []),
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
    );
  }
}
