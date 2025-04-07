// screen_login.dart
import 'package:flutter/material.dart';
import 'package:longevity_table/services/api_service.dart';
import 'dart:convert';


class LoginScreen extends StatefulWidget {
  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController idController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  
  void _login() async {
    final id = idController.text;
    final password = passwordController.text;

    if (id.isEmpty || password.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("아이디와 비밀번호를 입력해주세요.")),
      );
      return;
    }

    final response = await ApiService.loginUser(id: id, password: password);

    if (response.statusCode == 200) {
      final result = json.decode(response.body);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("로그인 성공: ${result['message']}")),
      );
      // TODO: 로그인 성공 후 이동할 페이지로 네비게이션 처리
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("로그인 실패: ${response.body}")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("로그인 화면")),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: idController,
              decoration: InputDecoration(labelText: '아이디'),
            ),
            TextField(
              controller: passwordController,
              decoration: InputDecoration(labelText: '비밀번호'),
              obscureText: true,
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _login,
              child: Text('로그인'),
            ),
          ],
        ),
      ),  
    );
  }
}

