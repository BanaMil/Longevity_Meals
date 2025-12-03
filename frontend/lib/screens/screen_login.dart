// screen_login.dart

import 'package:flutter/material.dart';
import 'package:frontend/services/service_auth.dart';
import 'package:frontend/models/user.dart';
import 'package:frontend/screens/screen_health_info/choice_info.dart';
import 'package:frontend/screens/screen_index.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController useridController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  void _login() async {
    final enteredId = useridController.text.trim();
    final password = passwordController.text.trim();

    debugPrint("🔐 Trying login with ID: '$enteredId', PW: '$password'");

    try {
    final User user = await AuthService.loginUser(
      userid: enteredId,
      password: password,
    );

    if (!mounted) return;
    debugPrint("✅ 로그인 성공: ${user.userid}, healthInfoSubmitted=${user.healthInfoSubmitted}");

    // 건강정보 입력 여부에 따라 분기
    if (user.healthInfoSubmitted == true) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => IndexScreen(userid: user.userid)),
      );
      debugPrint("건강정보 입력됨 → IndexScreen 이동");
    } else {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const ChoiceInfoScreen()),
      );
      debugPrint("건강정보 미입력 → ChoiceInfoScreen 이동");
    }
  } catch (e) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text("로그인 실패: $e")),
    );
  }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 251, 255, 228),
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 251, 255, 228),
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          child: Padding(
            padding: EdgeInsets.only(
              bottom: MediaQuery.of(context).viewInsets.bottom + 16,
            ),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Center(
                    child: ClipOval(
                      child: Image.asset(
                        'assets/images/longevity_meals_logo.png',
                        width: 150,
                        height: 150,
                        fit: BoxFit.cover,
                      ),
                    ),
                  ),
                  const SizedBox(height: 50),

                  SizedBox(
                    width: MediaQuery.of(context).size.width * 0.6,
                    child: TextFormField(
                      controller: useridController,
                      decoration: InputDecoration(labelText: '아이디'),
                      validator:
                          (value) => value!.isEmpty ? '아이디를 입력하세요' : null,
                    ),
                  ),
                  const SizedBox(height: 25),

                  SizedBox(
                    width: MediaQuery.of(context).size.width * 0.6,
                    child: TextFormField(
                      controller: passwordController,
                      decoration: InputDecoration(labelText: '비밀번호'),
                      validator:
                          (value) => value!.isEmpty ? '비밀번호를 입력하세요' : null,
                      obscureText: true,
                    ),
                  ),
                  const SizedBox(height: 50),
                  ElevatedButton(
                    onPressed: () async {
                      if (_formKey.currentState!.validate()) {
                        _login();
                      }
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color.fromARGB(255, 196, 215, 110),
                      minimumSize: const Size(150, 50), // 버튼 높이
                      textStyle: const TextStyle(fontSize: 20), // 폰트 크기
                    ),
                    child: Text(
                      '로그인',
                      style: TextStyle(
                        color: Colors.black,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
