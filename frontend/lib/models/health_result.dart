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
  final List<NutrientIntake> personalizedIntake; 

  HealthResult ({
    required this.userid,
    required this.recommendedNutrients,
    required this.restrictedNutrients,
    required this.personalizedIntake,
  });

  factory HealthResult.fromJson(Map<String, dynamic> json) {
    return HealthResult(
      userid: json['userid'], 
      recommendedNutrients: List<String>.from(json['recommendedNutrients']),
      restrictedNutrients: List<String>.from(json['restrictedNutrients']), 
      personalizedIntake: (json['personalizedIntake'] as List)
          .map((item) => NutrientIntake.fromJson(item))
          .toList(),
    );
  }
}