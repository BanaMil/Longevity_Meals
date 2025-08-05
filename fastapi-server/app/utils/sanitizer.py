def is_valid_food_item(item):
    return isinstance(item, dict) and "name" in item and "intake" in item

def sanitize_meal_items(meal_list):
    return [item for item in meal_list if is_valid_food_item(item)]

def sanitize_day_plan(day_plan):
    for meal_type in ["breakfast", "lunch", "dinner"]:
        day_plan[meal_type] = sanitize_meal_items(day_plan.get(meal_type, []))
    return day_plan
