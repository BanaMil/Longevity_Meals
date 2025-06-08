// tab_delivery.dart

import 'package:flutter/material.dart';
import 'package:frontend/widgets/gradient_background.dart';

class DeliveryTab extends StatefulWidget {
  const DeliveryTab({super.key});

  @override
  State<DeliveryTab> createState() => _DeliveryTabState();
}

class _DeliveryTabState extends State<DeliveryTab> {
  final double progressValue = 0.7;

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

                  SizedBox(
                    height: 45,
                    child: LayoutBuilder(
                      builder: (context, constraints) {
                        final double iconX =
                            constraints.maxWidth * progressValue;

                        return Stack(
                          clipBehavior: Clip.none,
                          children: [
                            Container(
                              height: 10,
                              decoration: BoxDecoration(
                                color: Colors.grey[300],
                                borderRadius: BorderRadius.circular(5),
                              ),
                            ),
                            Container(
                              height: 10,
                              width: iconX,
                              decoration: BoxDecoration(
                                color: const Color.fromARGB(255, 196, 215, 110),
                                borderRadius: BorderRadius.circular(5),
                              ),
                            ),
                            Positioned(
                              left: iconX - 16, // 아이콘 중앙 정렬 보정
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

                  const Text(
                    "밥상이 배송 중입니다.",
                    style: TextStyle(fontSize: 20, fontWeight: FontWeight.w500),
                  ),
                  const Divider(height: 40),
                  buildButton("배송 조회하기"),
                  buildButton("배송 날짜 변경하기"),
                  buildButton("배송 주소 변경하기"),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget buildButton(String text) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(vertical: 12),
      child: ElevatedButton(
        onPressed: () {},
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
