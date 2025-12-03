<<<<<<< HEAD
=======
// food_item.dart

>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
class FoodNutrient {
  final String name;
  final String unit;
  final double amount;

  FoodNutrient({
    required this.name,
    required this.unit,
    required this.amount,
  });

<<<<<<< HEAD
  factory FoodNutrient.fromJson(Map<String, dynamic> json) {
    return FoodNutrient(
      name: json['name'] ?? '',
      unit: json['unit'] ?? '',
      amount: (json['amount'] as num?)?.toDouble() ?? 0.0,
=======
  factory FoodNutrient.fromJson(Map<String,dynamic> json) {
    return FoodNutrient(
      name: json['name'], 
      unit: json['unit'], 
      amount: (json['amount'] as num).toDouble(),
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
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

<<<<<<< HEAD
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
=======

class FoodItem{
  final String name;
  final String imageUrl;
  final List<FoodNutrient> nutrients;
  final List<String>? ingredients;
  final String? recipe;

  FoodItem({
    required this.name,
    required this.imageUrl,
    required this.nutrients,
    required this.ingredients,
    required this.recipe,
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
  });

  factory FoodItem.fromJson(Map<String, dynamic> json) {
    return FoodItem(
      name: json['name'] ?? '',
<<<<<<< HEAD
      imageUrl: json['imageUrl'],
      nutrients: (json['nutrients'] as List?)?.map((e) => FoodNutrient.fromJson(e)).toList(),
      ingredients: (json['ingredients'] as List?)
          ?.map((e) => Map<String, String>.from(e))
          .toList(),
      servingCount: json['servingCount'],
      recipeSteps: (json['recipeSteps'] as List?)?.map((e) => e.toString()).toList(),
=======
      imageUrl: json['imageUrl'] ?? '',
      nutrients: (json['nutrients'] as List)
          .map((e) => FoodNutrient.fromJson(e))
          .toList(),
      ingredients: json['ingredients'] != null
          ? List<String>.from(json['ingredients'])
          : null,
      recipe: json['recipe'],
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'imageUrl': imageUrl,
<<<<<<< HEAD
      'nutrients': nutrients?.map((e) => e.toJson()).toList(),
      'ingredients': ingredients,
      'servingCount': servingCount,
      'recipeSteps': recipeSteps,
    };
  }
}
=======
      'nutrients': nutrients.map((e) => e.toJson()).toList(),
      'ingredients': ingredients,
      'recipe': recipe,
    };
  }
}
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
