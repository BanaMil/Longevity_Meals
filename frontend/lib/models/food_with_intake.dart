class FoodWithIntake {
  final String name;
  final double intake;

  FoodWithIntake({
    required this.name,
    required this.intake,
  });

  factory FoodWithIntake.fromJson(Map<String, dynamic> json) {
    return FoodWithIntake(
      name: json['name'] ?? '',
      intake: (json['intake'] as num).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'intake': intake,
    };
  }
}
