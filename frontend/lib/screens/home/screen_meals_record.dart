// screen_meals_record.dart

import 'package:flutter/material.dart';
import 'package:intl/intl.dart'; // 날짜 포맷용
import 'package:frontend/widgets/gradient_background.dart';

class MealsRecordScreen extends StatelessWidget {
  const MealsRecordScreen({super.key});

  // 날짜 리스트 생성(오늘 ~ 6일 전)
  List<Map<String, dynamic>> generateRecent7DaysRecords() {
    final now = DateTime.now();
    final List<Map<String, dynamic>> records = [];
    final formatter = DateFormat('M월 d일 (E)', 'ko_KR');

    for (int i = 0; i < 7; i++) {
      final date = now.subtract(Duration(days: i));
      final formatted = formatter.format(date);
      records.add({'date': formatted, 'meals': []});
    }
    return records;
  }

  @override
  Widget build(BuildContext context) {
    final records = generateRecent7DaysRecords();

    return GradientBackground(
      child: Scaffold(
        backgroundColor: Colors.transparent,
        appBar: AppBar(
          backgroundColor: Colors.transparent,
          elevation: 0,
          title: const Text(
            '밥상 기록',
            style: TextStyle(
              fontSize: 25,
              fontWeight: FontWeight.bold,
              color: Color.fromARGB(255, 66, 105, 50),
            ),
          ),
          centerTitle: true,
        ),
        body: Padding(
          padding: const EdgeInsets.fromLTRB(16, 10, 16, 16),
          child: Container(
            width: MediaQuery.of(context).size.width * 0.95,
            padding: const EdgeInsets.symmetric(horizontal: 25, vertical: 25),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(20),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Expanded(
                  child: SingleChildScrollView(
                    scrollDirection: Axis.vertical,

                    child: Table(
                      border: TableBorder.all(),

                      columnWidths: const {
                        0: FixedColumnWidth(130),
                        1: FlexColumnWidth(),
                      },
                      children: [
                        const TableRow(
                          decoration: BoxDecoration(
                            color: Color.fromARGB(255, 196, 215, 110),
                          ),
                          children: [
                            Padding(
                              padding: EdgeInsets.all(8),
                              child: Text(
                                '날짜',
                                textAlign: TextAlign.center,
                                style: TextStyle(
                                  fontSize: 20,
                                  color: Colors.black,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                            Padding(
                              padding: EdgeInsets.all(8),
                              child: Text(
                                '밥상',
                                textAlign: TextAlign.center,
                                style: TextStyle(
                                  fontSize: 20,
                                  color: Colors.black,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                          ],
                        ),

                        ...records.map((record) {
                          final meals = List<String>.from(record['meals']);
                          return TableRow(
                            decoration: const BoxDecoration(),
                            children: [
                              TableCell(
                                verticalAlignment:
                                    TableCellVerticalAlignment.middle,
                                child: Padding(
                                  padding: const EdgeInsets.all(8),
                                  child: Text(
                                    record['date'],
                                    style: TextStyle(fontSize: 18),
                                    textAlign: TextAlign.center,
                                  ),
                                ),
                              ),
                              TableCell(
                                verticalAlignment:
                                    TableCellVerticalAlignment.middle,
                                child: Padding(
                                  padding: const EdgeInsets.all(8),
                                  child:
                                      meals.isEmpty
                                          ? Center(
                                            child: IconButton(
                                              icon: Icon(
                                                Icons.add_circle_outline,
                                              ),
                                              onPressed: () {
                                                // 식단 추가 누르면 나오는 거 ~~
                                              },
                                            ),
                                          )
                                          : Wrap(
                                            alignment: WrapAlignment.center,
                                            spacing: 10,
                                            runSpacing: 4,
                                            children:
                                                meals.map((meal) {
                                                  return GestureDetector(
                                                    onTap: () {
                                                      // 항목 클릭 시 동작
                                                    },
                                                    child: Container(
                                                      padding:
                                                          const EdgeInsets.symmetric(
                                                            horizontal: 14,
                                                            vertical: 16,
                                                          ),
                                                      decoration: BoxDecoration(
                                                        border: Border.all(
                                                          color: Color.fromARGB(
                                                            255,
                                                            66,
                                                            105,
                                                            50,
                                                          ),
                                                          width: 2,
                                                        ),
                                                        borderRadius:
                                                            BorderRadius.circular(
                                                              6,
                                                            ),
                                                      ),
                                                      child: Text(
                                                        meal,
                                                        style: TextStyle(
                                                          fontSize: 15,
                                                        ),
                                                      ),
                                                    ),
                                                  );
                                                }).toList(),
                                          ),
                                ),
                              ),
                            ],
                          );
                        }),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
