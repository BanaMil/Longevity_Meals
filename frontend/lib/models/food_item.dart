// food_item.dart

class FoodNutrient {
  final String name;
  final String unit;
  final double amount;

  FoodNutrient({
    required this.name,
    required this.unit,
    required this.amount,
  });

  factory FoodNutrient.fromJson(Map<String,dynamic> json) {
    return FoodNutrient(
      name: json['name'], 
      unit: json['unit'], 
      amount: (json['amount'] as num).toDouble(),
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
  });

  factory FoodItem.fromJson(Map<String, dynamic> json) {
    return FoodItem(
      name: json['name'] ?? '',
      imageUrl: json['imageUrl'] ?? '',
      nutrients: (json['nutrients'] as List)
          .map((e) => FoodNutrient.fromJson(e))
          .toList(),
      ingredients: json['ingredients'] != null
          ? List<String>.from(json['ingredients'])
          : null,
      recipe: json['recipe'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'imageUrl': imageUrl,
      'nutrients': nutrients.map((e) => e.toJson()).toList(),
      'ingredients': ingredients,
      'recipe': recipe,
    };
  }
}