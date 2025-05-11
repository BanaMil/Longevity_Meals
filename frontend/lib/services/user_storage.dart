// user_storage.dart

import 'package:shared_preferences/shared_preferences.dart';

class UserStorage {
  static Future<void> saveUserInfo({
    required String username,
    required String id,
    required String address,
  }) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('username', username);
    await prefs.setString('id', id);
    await prefs.setString('address', address);
  }

  static Future<Map<String, String?>> loadUserInfo() async {
    final prefs = await SharedPreferences.getInstance();
    return {
      'username': prefs.getString('username'),
      'id': prefs.getString('id'),
      'address': prefs.getString('address'),
    };
  }

  static Future<void> clearUserInfo() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('username');
    await prefs.remove('id');
    await prefs.remove('address');
  }
}

