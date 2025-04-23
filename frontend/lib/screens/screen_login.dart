// screen_login.dart
import 'package:flutter/material.dart';
import 'package:frontend/services/service_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';


class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController idController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  
  void _login() async {
    final id = idController.text;
    final password = passwordController.text;

    final response = await AuthService.loginUser(id: id, password: password);

    if (!mounted) return;

    if (response.statusCode == 200) {
      final result = json.decode(response.body);
      final token = result['token'];

      // 토큰 저장
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('jwt_token', token);

      if (!mounted) return;

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
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: idController,
                decoration: InputDecoration(labelText: '아이디'),
                validator: (value) => value!.isEmpty ? '아이디를 입력하세요' : null,
              ),
              TextFormField(
                controller: passwordController,
                decoration: InputDecoration(labelText: '비밀번호'),
                validator: (value) => value!.isEmpty ? '비밀번호를 입력하세요' : null,
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
      ),    
    );
  }
}