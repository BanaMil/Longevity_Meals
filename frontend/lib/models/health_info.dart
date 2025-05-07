// health_info.dart

class HealthInfo {
  String? id;
  double? height;
  double? weight;
  List<String> diseases = [];
  List<String> allergies = [];
  List<String> dislikes = [];

  Map<String, dynamic> toJson() {
    return {
      'id' : id,
      'height': height,
      'weight': weight,
      'diseases': diseases,
      'allergies': allergies,
      'dislikes': dislikes,
    };
  }
}