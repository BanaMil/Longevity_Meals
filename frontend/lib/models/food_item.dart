class FoodNutrient {
  final String name;
  final String unit;
  final double amount;

  FoodNutrient({
    required this.name,
    required this.unit,
    required this.amount,
  });

  factory FoodNutrient.fromJson(Map<String, dynamic> json) {
    return FoodNutrient(
      name: json['name'] ?? '',
      unit: json['unit'] ?? '',
      amount: (json['amount'] as num?)?.toDouble() ?? 0.0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'unit': unit,
      'amount': amount,
    };
  }
}

class FoodItem {
  final String name;
  final String? imageUrl;
  final List<FoodNutrient>? nutrients;
  final List<Map<String, String>>? ingredients;
  final int? servingCount;
  final List<String>? recipeSteps;

  FoodItem({
    required this.name,
    this.imageUrl,
    this.nutrients,
    this.ingredients,
    this.servingCount,
    this.recipeSteps,
  });

  factory FoodItem.fromJson(Map<String, dynamic> json) {
    return FoodItem(
      name: json['name'] ?? '',
      imageUrl: json['imageUrl'],
      nutrients: (json['nutrients'] as List?)?.map((e) => FoodNutrient.fromJson(e)).toList(),
      ingredients: (json['ingredients'] as List?)
          ?.map((e) => Map<String, String>.from(e))
          .toList(),
      servingCount: json['servingCount'],
      recipeSteps: (json['recipeSteps'] as List?)?.map((e) => e.toString()).toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'imageUrl': imageUrl,
      'nutrients': nutrients?.map((e) => e.toJson()).toList(),
      'ingredients': ingredients,
      'servingCount': servingCount,
      'recipeSteps': recipeSteps,
    };
  }
}
