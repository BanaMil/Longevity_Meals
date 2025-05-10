// screen_health_result.dart

import 'package:flutter/material.dart';
import 'package:frontend/models/health_result.dart';
import 'package:frontend/services/service_health_result.dart';

class HealthResultScreen extends StatefulWidget {
  final String id;
  const HealthResultScreen({super.key, required this.id});

  @override
  State<HealthResultScreen> createState() => _HealthResultScreenState();
}

class _HealthResultScreenState extends State<HealthResultScreen> {
  late Future<HealthResult> analysisFuture;

  @override
  void initState() {
    super.initState();
    analysisFuture = HealthResultService.fetchAnalysisResult(widget.id);
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<HealthResult>(
      future: analysisFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Scaffold(body: Center(child: CircularProgressIndicator()));
        } else if (snapshot.hasError) {
          return Scaffold(body: Center(child: Text("오류발생: ${snapshot.error}")));
        } else if (snapshot.hasData) {
          final data = snapshot.data!;
          return Scaffold(
            appBar: AppBar(title: const Text('건강정보'), centerTitle: true),
            body: SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Row(
                    children: [
                      const Icon(Icons.arrow_back),
                      const SizedBox(width: 8),
                      Text(
                        "'${data.username}' 님의 건강정보\n분석 결과입니다.",
                        style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                      ),
                    ],
                  ),
                  const SizedBox(height: 24),
                  if (data.deficientNutrients.isNotEmpty)
                    _InfoCard("부족한 영양소는\n${data.deficientNutrients.join(', ')} 입니다."),
                  if (data.excessiveNutrients.isNotEmpty)
                    _InfoCard("${data.excessiveNutrients.join(', ')} 섭취는 줄이는 것이 좋습니다."),
                  if (data.riskDiseases.isNotEmpty)
                    _InfoCard("위험질환으로 ${data.riskDiseases.join(', ')} 이(가)있습니다."),
                  const SizedBox(height: 32),
                  const Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      Text("추천 음식", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                      Text("비추천 음식", style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      _FoodImageBox(data.recommendedFood),
                      _FoodImageBox(data.notRecommendedFood),
                    ],
                  ),
                ],
              ),
            ), 
          );
        } else {
          return const Scaffold(body: Center(child: Text("데이터 없음")));
        }
      },
    );
  }
}


// 내부 위젯
class _InfoCard extends StatelessWidget {
  final String text;
  const _InfoCard(this.text);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom:12),
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: Colors.green[100],
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(text, style: const TextStyle(fontSize: 16)),
    );
  }
}


class _FoodImageBox extends StatelessWidget {
  final String imagePath;
  const _FoodImageBox(this.imagePath);

  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: BorderRadius.circular(12),
      child: Image.asset(
        imagePath,
        width: 120,
        height: 100,
        fit: BoxFit.cover,
      ),
    );
  }
}