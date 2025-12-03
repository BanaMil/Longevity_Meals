import 'package:frontend/models/food_with_intake.dart';

class DailyMeals {
  final List<FoodWithIntake> breakfast;
  final List<FoodWithIntake> lunch;
  final List<FoodWithIntake> dinner;

  DailyMeals({
    required this.breakfast,
    required this.lunch,
    required this.dinner,
  });

  factory DailyMeals.fromJson(Map<String, dynamic> json) {
    return DailyMeals(
      breakfast: (json['breakfast'] as List<dynamic>)
          .map((item) => FoodWithIntake.fromJson(item))
          .toList(),
      lunch: (json['lunch'] as List<dynamic>)
          .map((item) => FoodWithIntake.fromJson(item))
          .toList(),
      dinner: (json['dinner'] as List<dynamic>)
          .map((item) => FoodWithIntake.fromJson(item))
          .toList(),
    );
  }
}
