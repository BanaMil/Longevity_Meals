// health_result.dart

/*
{
  "userid": "qq11",
  "recommendedNutrients": ["단백질", "식이섬유"],
  "restrictedNutrients": ["당류", "나트륨"],
  "cautionNutrients": ["포화지방산"],
  "personalizedIntake": [
    { "name": "단백질", "unit": "g", "amount": 56.2 },
    { "name": "칼슘", "unit": "mg", "amount": 800 },
    { "name": "에너지", "unit": "kcal", "amount": 2100 }
  ]
}
*/

// 사용자 맞춤 영양소 섭취량 
class NutrientIntake {
  final String name;
  final String unit;
  final double amount;

  NutrientIntake({
    required this.name,
    required this.unit,
    required this.amount,
  });

  factory NutrientIntake.fromJson(Map<String, dynamic> json) {
    return NutrientIntake(
      name: json['name'],
      unit: json['unit'], 
      amount: (json['amount'] as num).toDouble(),
    );
  }
}


class HealthResult {
  final String userid;
  final List<String> recommendedNutrients;
  final List<String> restrictedNutrients;
<<<<<<< HEAD
  final List<NutrientIntake> personalizedIntake; 

=======
  // final List<String> cautionNutrients;
  final List<NutrientIntake> personalizedIntake; 

  // final String recommendedFood;    // recommendedFoodImagePath로 할지 이야기 해보기 
  // final String notRecommendedFood; 

>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
  HealthResult ({
    required this.userid,
    required this.recommendedNutrients,
    required this.restrictedNutrients,
<<<<<<< HEAD
    required this.personalizedIntake,
=======
    // required this.cautionNutrients,
    required this.personalizedIntake,
    // required this.recommendedFood,
    // required this.notRecommendedFood,
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
  });

  factory HealthResult.fromJson(Map<String, dynamic> json) {
    return HealthResult(
      userid: json['userid'], 
      recommendedNutrients: List<String>.from(json['recommendedNutrients']),
      restrictedNutrients: List<String>.from(json['restrictedNutrients']), 
<<<<<<< HEAD
      personalizedIntake: (json['personalizedIntake'] as List)
          .map((item) => NutrientIntake.fromJson(item))
          .toList(),
=======
      // cautionNutrients: List<String>.from(json['cautionNutrients']),
      personalizedIntake: (json['personalizedIntake'] as List)
          .map((item) => NutrientIntake.fromJson(item))
          .toList(),
      // recommendedFood: json['recommendedFood'], 
      // notRecommendedFood: json['notRecommendedFood'],
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
    );
  }
}