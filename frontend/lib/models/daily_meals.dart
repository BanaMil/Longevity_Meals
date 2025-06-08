// daily_meals.dart

class DailyMeals {
  final List<String> breakfast;
  final List<String> lunch;
  final List<String> dinner;

  DailyMeals({
    required this.breakfast,
    required this.lunch,
    required this.dinner,
  });

  factory DailyMeals.fromJson(Map<String, dynamic> json) {
    return DailyMeals(
      breakfast: List<String>.from(json['breakfast'] ?? []),
      lunch: List<String>.from(json['lunch'] ?? []),
      dinner: List<String>.from(json['dinner'] ?? []),
    );
  }
}
