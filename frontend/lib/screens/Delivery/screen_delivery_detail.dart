// screen_delivery_detail.dart

import 'package:flutter/material.dart';

class DeliveryDetailScreen extends StatelessWidget {
  const DeliveryDetailScreen({super.key});

  static const Color primaryColor = Color.fromARGB(255, 86, 128, 52);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.pop(context),
        ),
        title: const Text("배송 조회"),
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: Padding(
        padding: const EdgeInsets.fromLTRB(16, 10, 16, 20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              "밥상이 '배송 중' 입니다.",
              style: TextStyle(
                fontSize: 18, fontWeight: FontWeight.w500
              ),
            ),
            const Divider(height: 30),
            const SizedBox(height: 24),

              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: const [
                  _StepIcon(label: "주문 접수", icon: Icons.receipt_long),
                  _StepArrow(),
                  _StepIcon(label: "준비 중", icon: Icons.storefront),
                  _StepArrow(),
                  _StepIcon(label: "배송 중", icon: Icons.local_shipping, active: true),
                  _StepArrow(),
                  _StepIcon(label: "배송완료", icon: Icons.home),
                ],
              ),
              const SizedBox(height: 24),

              // 아래 타임라인
              Expanded(
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
                  decoration: BoxDecoration(
                    color: const Color(0xFFF7F7F7),
                    borderRadius: BorderRadius.circular(16),
                  ),
                  child: Column(
                    children: const [
                    _TimelineEntry(
                      title: "배송완료",
                      time1: "2025.09.03 09:40",
                      desc1: "오리역",
                      time2: "2025.09.03 09:30",
                      desc2: "정자역",
                      isFirst: true,
                      isLast: false,
                      state: _TimelineState.future,
                    ),
                    _TimelineEntry(
                      title: "배송 중",
                      time1: "2025.09.03 09:20",
                      desc1: "배송 시작",
                      isFirst: false,
                      isLast: false,
                      state: _TimelineState.current,
                    ),
                    _TimelineEntry(
                      title: "준비 중",
                      time1: "2025.09.03 09:05",
                      desc1: "밥상 준비 완료",
                      isFirst: false,
                      isLast: false,
                      state: _TimelineState.past,
                    ),
                    _TimelineEntry(
                      title: "주문 접수",
                      time1: "2025.09.02 13:28",
                      desc1: "밥상 신청 완료",
                      isFirst: false,
                      isLast: true,
                      state: _TimelineState.past,
                    ),
                  ],
                ),
              ),
              ),
          ],
        ),
      ),
    );
  }
}


// 상단 단계 아이콘
class _StepIcon extends StatelessWidget {
  final String label;
  final IconData icon;
  final bool active;

  const _StepIcon({
    required this.label,
    required this.icon,
    this.active = false,
  });

  static const Color primaryColor = DeliveryDetailScreen.primaryColor;

  @override
  Widget build(BuildContext context) {
    final Color circleColor = active ? primaryColor : Colors.grey.shade400;
    final Color bgColor = active ? primaryColor : Colors.white;
    final Color iconColor = active ? Colors.white : circleColor;
    final Color labelColor = active ? primaryColor : Colors.grey.shade600;

    return Column(
      children: [
        Container(
          width: 46,
          height: 46,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: bgColor,
            border: Border.all(color: circleColor, width: 2),
          ),
          child: Icon(icon, size: 24, color: iconColor),
        ),
        const SizedBox(height: 6),
        Text(
          label,
          style: TextStyle(
            fontSize: 12,
            color: labelColor,
          ),
        ),
      ],
    );
  }
}

// 상단 단계 사이 화살표
class _StepArrow extends StatelessWidget {
  const _StepArrow();

  @override
  Widget build(BuildContext context) {
    return Icon(
      Icons.chevron_right,
      size: 20,
      color: Colors.grey.shade400,
    );
  }
}

enum _TimelineState { past, current, future }

// 타임라인
class _TimelineEntry extends StatelessWidget {
  final String title;
  final String time1;
  final String desc1;
  final String? time2;
  final String? desc2;
  final bool isFirst;
  final bool isLast;
  final _TimelineState state;

  const _TimelineEntry({
    required this.title,
    required this.time1,
    required this.desc1,
    this.time2,
    this.desc2,
    required this.isFirst,
    required this.isLast,
    required this.state,
  });

  static const Color primaryColor = DeliveryDetailScreen.primaryColor;

  @override
  Widget build(BuildContext context) {
    final bool isPast = state == _TimelineState.past;
    final bool isCurrent = state == _TimelineState.current;

    Color circleBorder;
    Color circleFill;
    Widget? circleChild;

    if (isCurrent) {
      circleBorder = primaryColor;
      circleFill = primaryColor;
      circleChild = const Icon(Icons.check, size: 16, color: Colors.white);
    } else if (isPast) {
      circleBorder = primaryColor;
      circleFill = Colors.white;
      circleChild = const Icon(Icons.check, size: 16, color: primaryColor);
    } else {
      // future
      circleBorder = Colors.grey.shade400;
      circleFill = Colors.white;
    }

    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // 왼쪽 세로 라인 + 동그라미
        Column(
          children: [
            if (!isFirst)
              Container(
                width: 2,
                height: 16,
                color: Colors.grey.shade300,
              ),
            Container(
              width: 26,
              height: 26,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: circleFill,
                border: Border.all(color: circleBorder, width: 2),
              ),
              child: circleChild,
            ),
            if (!isLast)
              Container(
                width: 2,
                height: 34,
                color: Colors.grey.shade300,
              ),
          ],
        ),
        const SizedBox(width: 12),

        // 오른쪽 텍스트
        Expanded(
          child: Padding(
            padding: const EdgeInsets.only(bottom: 18),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: TextStyle(
                    color: isCurrent
                        ? primaryColor
                        : Colors.grey.shade700,
                    fontWeight: isCurrent ? FontWeight.bold : FontWeight.w500,
                    fontSize: 14,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  time1,
                  style: const TextStyle(fontSize: 12, color: Colors.black87),
                ),
                Text(
                  desc1,
                  style: const TextStyle(fontSize: 12, color: Colors.black87),
                ),
                if (time2 != null && desc2 != null) ...[
                  const SizedBox(height: 2),
                  Text(
                    time2!,
                    style:
                        const TextStyle(fontSize: 12, color: Colors.black87),
                  ),
                  Text(
                    desc2!,
                    style:
                        const TextStyle(fontSize: 12, color: Colors.black87),
                  ),
                ],
              ],
            ),
          ),
        ),
      ],
    );
  }
}