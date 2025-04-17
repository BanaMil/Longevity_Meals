//user.dart
class User {
  final String username;
  final String id;
  final String password;
  final DateTime birthdate;

  User({
    required this.username,
    required this.id,
    required this.password,
    required this.birthdate,
  });

  // JSON → User (서버에서 받은 데이터 파싱)
  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      username: json['username'] ?? '',
      id: json['id'] ?? '',
      password: json['password'] ?? '',
      birthdate: DateTime.parse(json['birthdate']),
    );
  }

  // User → JSON (서버로 데이터 전송할 때)
  Map<String, dynamic> toJson() {
    return {
      'username': username,
      'id': id,
      'password': password,
      'birthdate': birthdate.toIso8601String(),
    };
  }
}
