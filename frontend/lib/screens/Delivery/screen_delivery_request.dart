// screen_delivery_request.dart

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:frontend/models/daily_meals.dart';
import 'package:frontend/models/food_with_intake.dart';
import 'package:frontend/services/service_delivery.dart';

class DeliveryRequestScreen extends StatefulWidget {
  final String userid;
  final Map<String, DailyMeals> mealsByDate;

  const DeliveryRequestScreen({
    super.key,
    required this.userid,
    required this.mealsByDate,
  });

  @override
  State<DeliveryRequestScreen> createState() => _DeliveryRequestScreenState();
}

class _DeliveryRequestScreenState extends State<DeliveryRequestScreen> {

  // 날짜별/끼니별 체크 상태
  final Map<String, bool> _checked = {};

  @override
  void initState() {
    super.initState();
    for (final entry in widget.mealsByDate.entries) {
      final date = entry.key;
      _checked['${date}_breakfast'] = true;
      _checked['${date}_lunch'] = true;
      _checked['${date}_dinner'] = true;
    }
  }

  @override
  Widget build(BuildContext context) {
    final sortedDates = widget.mealsByDate.keys.toList()..sort();

    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 235, 239, 165),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.black),
          onPressed: () => Navigator.pop(context),
        ),
        centerTitle: true,
        title: const Text(
          '배송 요청',
          style: TextStyle(
            fontSize: 22,
            fontWeight: FontWeight.bold,
            color: Colors.black,
          ),
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.fromLTRB(16, 10, 16, 16),
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 18),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(20),
          ),
          child: Column(
            children: [
              const Text(
                "배송 요청할 식단을 선택한 후\n'배송 요청하기' 버튼을 눌러주세요.",
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 14, color: Colors.black54),
              ),
              const SizedBox(height: 10),

              Expanded(
                child: ListView.separated(
                  itemCount: sortedDates.length,
                  separatorBuilder: (_, __) => const SizedBox(height: 18),
                  itemBuilder: (context, index) {
                    final dateKey = sortedDates[index];
                    final parsedDate = DateTime.parse(dateKey);
                    final formattedDate = DateFormat(
                      'M월 d일 (E)',
                      'ko_KR',
                    ).format(parsedDate);

                    final daily = widget.mealsByDate[dateKey]!;
                    return _buildDateBlock(formattedDate, dateKey, daily);
                  },
                ),
              ),
              const SizedBox(height: 10),

              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _submitRequest,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color.fromARGB(255, 196, 215, 110),
                    minimumSize: const Size(150, 50),
                    textStyle: const TextStyle(fontSize: 18),
                  ),
                  child: const Text(
                    '배송 요청하기',
                    style: TextStyle(
                      color: Colors.black,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildDateBlock(String formatted, String dateKey, DailyMeals daily) {
    return Column(
      children: [
        Center(
          child: Text(
          formatted,
          style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w600)),
        ),
        const SizedBox(height: 8),
        _requestCard('아침 밥상', '${dateKey}_breakfast', daily.breakfast),
        const SizedBox(height: 8),
        _requestCard('점심 밥상', '${dateKey}_lunch', daily.lunch),
        const SizedBox(height: 8),
        _requestCard('저녁 밥상', '${dateKey}_dinner', daily.dinner),
      ],
    );
  }

  Widget _requestCard(
    String title,
    String selectKey,
    List<FoodWithIntake> items,
  ) {
    final checked = _checked[selectKey] ?? true;

    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        border: Border.all(color: const Color.fromARGB(255, 66, 105, 50)),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 90,
            child: Text(
              title,
              style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children:
                  items
                      .map(
                        (e) =>
                            Text(e.name, style: const TextStyle(fontSize: 14)),
                      )
                      .toList(),
            ),
          ),
          // 체크박스
          Checkbox(
            value: checked,
            onChanged: (v) {
              setState(() {
                _checked[selectKey] = v ?? false;
              });
            },
          ),
        ],
      ),
    );
  }

  Future<void> _submitRequest() async {
    // 선택된 끼니만 수집
    final Map<String, List<String>> requestPayload = {};
    for (final key in _checked.keys) {
      if (_checked[key] == true) {
        final parts = key.split('_'); // [date, meal]
        final date = parts[0];
        final meal = parts[1]; // breakfast/lunch/dinner
        requestPayload.putIfAbsent(date, () => []);
        requestPayload[date]!.add(meal);
      }
    }

    final ok = await DeliveryService.request(widget.userid, requestPayload);

    if (!mounted) return;
    if (ok) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('배송 요청이 전송되었습니다.')));
      // debugPrint("선택된 배송 요청: $requestPayload");
      Navigator.pop(context);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('전송에 실패했습니다. 잠시 후 다시 시도하세요.')),
      );
    }
  }
}
