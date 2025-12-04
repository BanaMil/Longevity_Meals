// screen_results_view.dart

import 'package:flutter/material.dart';
import 'package:frontend/models/food_item.dart';
import 'package:frontend/screens/Search/screen_recipe_detail.dart';

class SearchResultsView extends StatefulWidget {
  final List<FoodItem> results;
  final String keyword;

  const SearchResultsView({
    super.key,
    required this.results,
    required this.keyword,
  });

  @override
  State<SearchResultsView> createState() => _SearchResultsViewState();
}

class _SearchResultsViewState extends State<SearchResultsView> {
  int _currentPage = 0;
  static const int _pageSize = 3;

  @override
  void didUpdateWidget(covariant SearchResultsView oldWidget) {
    super.didUpdateWidget(oldWidget);
    final totalPages = (widget.results.length / _pageSize).ceil();
    if (_currentPage >= totalPages && totalPages > 0) {
      setState(() => _currentPage = 0);
    }
  }

  List<FoodItem> get _currentItems {
    final start = _currentPage * _pageSize;
    final end = start + _pageSize;
    return widget.results.sublist(
      start,
      end > widget.results.length ? widget.results.length : end,
    );
  }

  bool get _hasNextPage =>
      (_currentPage + 1) * _pageSize < widget.results.length;
  bool get _hasPrevPage => _currentPage > 0;

  void _goToNextPage() {
    if (_hasNextPage) {
      setState(() => _currentPage++);
    }
  }

  void _goToPrevPage() {
    if (_hasPrevPage) {
      setState(() => _currentPage--);
    }
  }

  // 최대 5개 재료 선택:
  List<String> _selectIngredientsForDisplay(FoodItem item, String keyword) {
    final ings = item.ingredients;
    if (ings == null || ings.isEmpty) return const [];

    final names = ings.map((m) => (m['name'] ?? '').toString()).toList();

    if (names.length <= 5) return names;

    final key = keyword.trim().toLowerCase();

    int exactIdx = names.indexWhere((n) => n.toLowerCase() == key);
    int containIdx = exactIdx >= 0
        ? -1
        : names.indexWhere((n) => n.toLowerCase().contains(key));

    final firstFive = names.take(5).toList();

    if (exactIdx >= 0) {
      if (exactIdx < 5) return firstFive;
      firstFive[4] = names[exactIdx];
      return firstFive;
    }

    if (containIdx >= 0) {
      if (containIdx < 5) return firstFive;
      firstFive[4] = names[containIdx];
      return firstFive;
    }

    return firstFive;
  }

  // 재료: 이름1, 이름2, ...
  // 검색어 하이라이트(형광펜 효과)

  Widget _ingredientsLine(FoodItem item, String keyword) {
    final selected = _selectIngredientsForDisplay(item, keyword);
    if (selected.isEmpty) return const SizedBox.shrink();

    final key = keyword.trim().toLowerCase();

    final spans = <TextSpan>[const TextSpan(text: '재료: ')];
    for (int i = 0; i < selected.length; i++) {
      final name = selected[i];
      final isExact = key.isNotEmpty && name.toLowerCase() == key;

      spans.add(
        TextSpan(
          text: name,
          style: isExact
              ? const TextStyle(
                  backgroundColor: Colors.lightGreenAccent,
                  color: Colors.black,
                )
              : const TextStyle(color: Colors.black87),
        ),
      );
      if (i != selected.length - 1) {
        spans.add(const TextSpan(text: ', '));
      }
    }

    // 원본 재료가 더 있으면 ... 표시
    final totalCount = (item.ingredients?.length ?? 0);
    if (totalCount > selected.length) {
      spans.add(const TextSpan(text: '...'));
    }

    return RichText(
      maxLines: 2,
      overflow: TextOverflow.ellipsis,
      text: TextSpan(
        style: const TextStyle(fontSize: 13, color: Colors.black87),
        children: spans,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Expanded(
          child: (widget.results.isEmpty || _currentItems.isEmpty)
              ? const Center(
                  child: Text(
                    '레시피 정보가 없습니다.',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.w800,
                    ),
                  ),
                )
              : ListView.builder(
                  itemCount: _currentItems.length,
                  itemBuilder: (context, index) {
                    final item = _currentItems[index];

                    return Card(
                      margin: const EdgeInsets.symmetric(
                        vertical: 10,
                        horizontal: 10,
                      ),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(16),
                      ),
                      child: InkWell(
                        borderRadius: BorderRadius.circular(16),
                        onTap: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (_) => RecipeDetailScreen(item: item),
                            ),
                          );
                        },
                        child: SizedBox(
                          height: 150,
                          child: Row(
                            children: [
                              Padding(
                                padding: const EdgeInsets.only(left: 8),
                                child: item.imageUrl != null &&
                                        item.imageUrl!.isNotEmpty
                                    ? ClipRRect(
                                        borderRadius: BorderRadius.circular(12),
                                        child: Image.network(
                                          item.imageUrl!,
                                          width: 110,
                                          height: 110,
                                          fit: BoxFit.cover,
                                        ),
                                      )
                                    : const Icon(Icons.food_bank, size: 80),
                              ),
                              const SizedBox(width: 12),
                              Expanded(
                                child: Padding(
                                  padding: const EdgeInsets.symmetric(
                                    vertical: 10,
                                  ),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        item.name,
                                        style: const TextStyle(
                                          fontSize: 18,
                                          fontWeight: FontWeight.bold,
                                        ),
                                        maxLines: 1,
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                      const SizedBox(height: 6),
                                      Expanded(
                                        child: _ingredientsLine(
                                          item,
                                          widget.keyword,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    );
                  },
                ),
        ),

        // 페이지 이동 버튼
        if (widget.results.isEmpty)
          const SizedBox.shrink()
        else
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              if (_hasPrevPage)
                ElevatedButton(
                  onPressed: _goToPrevPage,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color.fromARGB(255, 235, 239, 165),
                  ),
                  child: const Text(
                    '이전',
                    style: TextStyle(color: Colors.black),
                  ),
                )
              else
                const SizedBox(width: 80),
              Text(
                '${_currentPage + 1} / ${(widget.results.length / _pageSize).ceil()}',
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
              if (_hasNextPage)
                ElevatedButton(
                  onPressed: _goToNextPage,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color.fromARGB(255, 235, 239, 165),
                  ),
                  child: const Text(
                    '다음',
                    style: TextStyle(color: Colors.black),
                  ),
                )
              else
                const SizedBox(width: 80),
            ],
          ),
      ],
    );
  }
}
