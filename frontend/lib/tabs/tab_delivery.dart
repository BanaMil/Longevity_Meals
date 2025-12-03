// tab_delivery.dart

import 'package:flutter/material.dart';
import 'package:frontend/models/daily_meals.dart';
import 'package:frontend/screens/Delivery/screen_delivery_meals.dart';
import 'package:frontend/widgets/gradient_background.dart';
import 'package:frontend/screens/Delivery/screen_delivery_detail.dart';
import 'package:frontend/screens/Delivery/screen_delivery_request.dart';
import 'package:frontend/screens/Delivery/screen_change_address.dart';
import 'package:frontend/services/service_meal.dart';

class DeliveryTab extends StatefulWidget {
  final String userid;

  const DeliveryTab({super.key, required this.userid});

  @override
  State<DeliveryTab> createState() => _DeliveryTabState();
}

class _DeliveryTabState extends State<DeliveryTab> {
  // 0.0 ~ 1.0 배송 진행 상태 표시
  final double progressValue = 0.7;

  // 배송 진행 단계 
  static const List<String> _steps = ['주문 접수', '준비 중', '배송 중', '배송 완료'];

  @override
  Widget build(BuildContext context) {
    return GradientBackground(
      child: Scaffold(
        backgroundColor: Colors.transparent,
        body: SafeArea(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(16, 10, 16, 20),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 25, vertical: 25),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Center(
                    child: Text(
                      '배송 화면',
                      style: TextStyle(
                        fontSize: 30,
                        fontWeight: FontWeight.bold,
                        color: Color.fromARGB(255, 66, 105, 50),
                      ),
                    ),
                  ),
                  const SizedBox(height: 40),

                  DeliveryProgressBar(
                    progress: progressValue, 
                    steps: _steps,           
                  ),

                  const Divider(height: 40),

                  buildButton(
                    "배송 요청하기",
                    onPressed: _openDeliveryRequestWithMeals,
                  ),
                  buildButton(
                    "배송 조회하기",
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const DeliveryDetailScreen(),
                        ),
                      );
                    },
                  ),
                  buildButton(
                    "배송 중인 식단 확인하기",
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder:
                              (_) => DeliveryMealsScreen(userid: widget.userid),
                        ),
                      );
                    },
                  ),
                  buildButton(
                    "배송 주소 변경하기",
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder:
                              (_) => ChangeAddressScreen(userid: widget.userid),
                        ),
                      );
                    },
                    ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  // 일주일 식단 불러온 후 배송 요청 화면으로 이동
  Future<void> _openDeliveryRequestWithMeals() async {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => const Center(child: CircularProgressIndicator()),
    );

    try {
      final Map<String, DailyMeals> mealsByDate =
          await MealService.fetchWeeklyMeals(widget.userid);

      if (!mounted) return;
      Navigator.pop(context); 

      Navigator.push(
        context,
        MaterialPageRoute(
          builder: (_) => DeliveryRequestScreen(
            userid: widget.userid,
            mealsByDate: mealsByDate,
          ),
        ),
      );
    } catch (e) {
      if (!mounted) return;
      Navigator.pop(context); 
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('주간 식단 불러오기 실패: $e')),
      );
    }
  }

  Widget buildButton(String text, {VoidCallback? onPressed}) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(vertical: 12),
      child: ElevatedButton(
        onPressed: onPressed,
        style: ElevatedButton.styleFrom(
          backgroundColor: const Color.fromARGB(255, 196, 215, 110),
          foregroundColor: Colors.black,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(15),
          ),
          elevation: 0,
          padding: const EdgeInsets.symmetric(vertical: 16),
        ),
        child: Text(text, style: TextStyle(fontSize: 20)),
      ),
    );
  }
}

// 배송 상태 진행 바 위젯
class DeliveryProgressBar extends StatelessWidget {
  final double progress;          
  final List<String> steps;       

  const DeliveryProgressBar({
    super.key,
    required this.progress,
    required this.steps,
  });

  @override
  Widget build(BuildContext context) {
    final int lastIndex = steps.length - 1;

    final int stepIndex = (progress * lastIndex).clamp(0, lastIndex).round();

    // 표시 문구: 예) 밥상이 ‘배송 중’ 입니다.
    final String statusText = steps[stepIndex];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          height: 45,
          child: LayoutBuilder(
            builder: (context, constraints) {
              final double snappedT = stepIndex / lastIndex;
              final double iconX = constraints.maxWidth * snappedT;

              return Stack(
                clipBehavior: Clip.none,
                children: [
                  // 바탕 라인
                  Container(
                    height: 6,
                    decoration: BoxDecoration(
                      color: Colors.grey[300],
                      borderRadius: BorderRadius.circular(3),
                    ),
                  ),
                  // 진행 라인 (현재 단계까지 채움)
                  Container(
                    height: 6,
                    width: iconX,
                    decoration: BoxDecoration(
                      color: const Color.fromARGB(255, 196, 215, 110),
                      borderRadius: BorderRadius.circular(3),
                    ),
                  ),
                  // 단계 점들
                  Positioned.fill(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: List.generate(steps.length, (i) {
                        final bool filled = i <= stepIndex;
                        return Container(
                          width: 14,
                          height: 14,
                          decoration: BoxDecoration(
                            color: filled
                                ? const Color.fromARGB(255, 196, 215, 110)
                                : Colors.white,
                            border: Border.all(
                              color: const Color.fromARGB(255, 196, 215, 110),
                              width: 2,
                            ),
                            shape: BoxShape.circle,
                          ),
                        );
                      }),
                    ),
                  ),
                  // 트럭 아이콘
                  Positioned(
                    left: iconX - 16,
                    top: -25,
                    child: const Icon(
                      Icons.local_shipping,
                      size: 32,
                      color: Color.fromARGB(255, 66, 105, 50),
                    ),
                  ),
                ],
              );
            },
          ),
        ),
        const SizedBox(height: 8),
        Text(
          "밥상이 ‘$statusText’ 입니다.",
          style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w500),
        ),
      ],
    );
  }
}

