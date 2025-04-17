// health_info.dart
class HealthInfo {
  double? height;
  double? weight;
  List<String> diseases = [];
  List<String> allergies = [];
  List<String> dislikes = [];

  Map<String, dynamic> toJson() {
    return {
      'height': height,
      'weight': weight,
      'diseases': diseases,
      'allergies': allergies,
      'dislikes': dislikes,
    };
  }
}