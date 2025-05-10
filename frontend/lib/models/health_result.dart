// health_result.dart

class HealthResult {
  final String username;
  final List<String> deficientNutrients;
  final List<String> excessiveNutrients;
  final List<String> riskDiseases; // 뺄지 말지 이야기 해보기
  final String recommendedFood;    // recommendedFoodImagePath로 할지 이야기 해보기 
  final String notRecommendedFood; 

  HealthResult ({
    required this.username,
    required this.deficientNutrients,
    required this.excessiveNutrients,
    required this.riskDiseases,
    required this.recommendedFood,
    required this.notRecommendedFood,
  });

  factory HealthResult.fromJson(Map<String, dynamic> json) {
    return HealthResult(
      username: json['username'], 
      deficientNutrients: List<String>.from(json['deficientNutrients']),
      excessiveNutrients: List<String>.from(json['excessiveNutrients']), 
      riskDiseases: List<String>.from(json['riskDiseases']), 
      recommendedFood: json['recommendedFood'], 
      notRecommendedFood: json['notRecommendedFood'],
    );
  }
}


