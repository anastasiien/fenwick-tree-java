public class Main {

    // фиксированные массивы
    private static FenwickTree[] trees = new FenwickTree[10];
    private static int[][] arrays = new int[10][];
    private static String[] names = new String[10];
    private static int treeCount = 0;
    private static int currentTreeIndex = -1;

    // вспомогательные методы (замена Scanner и Arrays)

    // чтение строки
    private static String readLine() {
        try {
            return System.console().readLine();
        } catch (Exception e) {
            return "";
        }
    }

    // чтение целого числа
    private static int readInt() throws Exception {
        String line = readLine();
        if (line == null || line.length() == 0) throw new Exception("Пустой ввод.");

        line = trimString(line);
        if (line.length() == 0) throw new Exception("Пустой ввод.");

        int result = 0;
        int sign = 1;
        int startIndex = 0;

        if (line.charAt(0) == '-') {
            sign = -1;
            startIndex = 1;
        }

        for (int i = startIndex; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c >= '0' && c <= '9') {
                result = result * 10 + (c - '0');
            } else {
                throw new Exception("Некорректный символ. Введите только целое число.");
            }
        }
        return result * sign;
    }

    // обрезка пробелов
    private static String trimString(String s) {
        if (s == null || s.length() == 0) return "";
        int start = 0;
        int end = s.length() - 1;

        while (start <= end && s.charAt(start) <= ' ') start++;
        while (end >= start && s.charAt(end) <= ' ') end--;

        if (start > end) return "";
        return s.substring(start, end + 1);
    }

    // разделение строки по пробелу
    private static String[] splitString(String s, char delimiter) {
        s = trimString(s);
        if (s.length() == 0) return new String[0];

        // количество частей
        int count = 0;
        boolean inWord = false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != delimiter && !inWord) {
                count++;
                inWord = true;
            } else if (s.charAt(i) == delimiter) {
                inWord = false;
            }
        }

        String[] parts = new String[count];
        int currentPart = 0;
        int start = 0;

        // извлечение частей
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == delimiter) {
                if (i > start && trimString(s.substring(start, i)).length() > 0) {
                    parts[currentPart] = trimString(s.substring(start, i));
                    currentPart++;
                }
                start = i + 1;
            }
        }

        // последняя часть
        if (currentPart < parts.length) {
            String lastPart = trimString(s.substring(start, s.length()));
            if (lastPart.length() > 0) {
                parts[currentPart] = lastPart;
            }
        }

        return parts;
    }

    // ручная печать массива
    private static void printArray(int[] arr) {
        if (arr == null || arr.length == 0) {
            System.out.print("[]");
            return;
        }

        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.print("]");
    }

    //отображение списка существующих проектов массивов (деревьев)
    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n--- Доступные проекты массивов ---");
            if (treeCount == 0) System.out.println("Проекты массивов не созданы");
            else for (int i = 0; i < treeCount; i++) {
                System.out.print((i + 1) + ". " + names[i]);
                // текущий выбранный проект
                if (i == currentTreeIndex) System.out.print(" [текущий]");
                System.out.println("");
            }

            // меню действий
            System.out.println("\nВыберите действие:");
            System.out.println("1. Создать новый проект массива");
            System.out.println("2. Переключиться на проект по имени");
            System.out.println("3. Изменить имя проекта");
            System.out.println("4. Удалить проект по имени");
            System.out.println("5. Соединить два массива в новый проект");
            System.out.println("6. Операции на текущем массиве");
            System.out.println("7. Выход");
            System.out.print("> ");

            try {
                // читаем выбор пользователя через собственный метод readInt()
                int choice = readInt();
                switch (choice) {
                    case 1:
                        // создание нового fenwick дерева
                        createTree();
                        break;
                    case 2:
                        // переключение между существующими проектами
                        switchTree();
                        break;
                    case 3:
                        // переименование выбранного проекта
                        renameTree();
                        break;
                    case 4:
                        // удаление одного из проектов
                        deleteTree();
                        break;
                    case 5:
                        // создание нового проекта путём слияния двух существующих массивов
                        mergeTrees();
                        break;
                    case 6:
                        // подменю операций на дереве
                        if (currentTreeIndex == -1) {
                            System.out.println("Нет выбранного проекта массива.");
                        } else {
                            runOperations(currentTreeIndex);
                        }
                        break;
                    case 7:
                        // выход из программы
                        running = false;
                        break;
                    default:
                        // если выбор не соответствует пунктам меню пишется сообшение
                        System.out.println("Неверный выбор.");
                }
            } catch (Exception e) {
                // обработка ошибок ввода
                System.out.println("Ошибка ввода: " + e.getMessage());
            }
        }
    }

    // изменение имя проекта
    private static void renameTree() {
        if (treeCount == 0) {
            System.out.println("Нет проектов для переименования.");
            return;
        }

        System.out.print("Введите имя проекта, который нужно переименовать: ");
        String oldName = trimString(readLine());

        int index = -1;
        for (int i = 0; i < treeCount; i++) {
            if (names[i] != null && names[i].equals(oldName)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("Проект с именем '" + oldName + "' не найден.");
            return;
        }

        System.out.print("Введите новое имя для проекта: ");
        String newName = trimString(readLine());

        // проверка на уникальность нового имени
        for (int i = 0; i < treeCount; i++) {
            if (i != index && names[i] != null && names[i].equals(newName)) {
                System.out.println("Проект с именем '" + newName + "' уже существует. Переименование отменено.");
                return;
            }
        }

        names[index] = newName;
        System.out.println("Проект '" + oldName + "' успешно переименован в '" + newName + "'.");
    }

    // соединение двух массивов
    private static void mergeTrees() {
        if (treeCount < 2) {
            System.out.println("Нужно хотя бы два массива для соединения.");
            return;
        }
        if (treeCount >= trees.length) {
            System.out.println("Нет места для создания нового проекта массива.");
            return;
        }

        System.out.print("Введите имя первого проекта массива: ");
        String nameA = trimString(readLine());

        System.out.print("Введите имя второго проекта массива: ");
        String nameB = trimString(readLine());

        int indexA = -1;
        int indexB = -1;

        for (int i = 0; i < treeCount; i++) {
            if (names[i] != null && names[i].equals(nameA)) {
                indexA = i;
            }
            if (names[i] != null && names[i].equals(nameB)) {
                indexB = i;
            }
        }

        if (indexA == -1 || indexB == -1) {
            System.out.println("Один из проектов массива не найден.");
            return;
        }
        if (indexA == indexB) {
            System.out.println("Нельзя соединить проект массива сам с собой.");
            return;
        }

        // конкатенация массивов вручную
        int[] arrA = arrays[indexA];
        int[] arrB = arrays[indexB];
        int newSize = arrA.length + arrB.length;
        int[] mergedArr = new int[newSize];

        for (int i = 0; i < arrA.length; i++) {
            mergedArr[i] = arrA[i];
        }
        for (int i = 0; i < arrB.length; i++) {
            mergedArr[arrA.length + i] = arrB[i];
        }

        // создание нового имени и дерева
        String newName = nameA + "+" + nameB;
        FenwickTree newFt = new FenwickTree(newSize);
        newFt.build(mergedArr);

        // сохранение нового проекта
        trees[treeCount] = newFt;
        arrays[treeCount] = mergedArr;
        names[treeCount] = newName;
        currentTreeIndex = treeCount;
        treeCount++;

        System.out.println("Массивы успешно соединены. Новый Проект: '" + newName + "'.");
    }


    private static void createTree() {
        if (treeCount >= trees.length) {
            // проверяем, не достигли ли мы максимального количества проектов
            System.out.println("Достигнуто максимальное количество проектов. Удалите ненужные проекты.");
            return;
        }

        // ввод имени нового проекта
        System.out.print("Введите имя нового проекта массива: ");
        String name = readLine();
        name = trimString(name);

        // проверка уникальности имени проекта
        for (int i = 0; i < treeCount; i++) {
            if (names[i] != null && names[i].equals(name)) {
                System.out.println("Проект с именем '" + name + "' уже существует.");
                return;
            }
        }

        // ввод исходного массива
        System.out.print("Введите элементы массива через пробел: ");
        String inputLine = readLine();
        String[] parts = splitString(inputLine, ' '); // разбиения строки

        // преобразуем строки в целые числа
        int[] arr = new int[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (part == null || trimString(part).length() == 0) continue;

                int value = 0;
                int sign = 1;
                int startIndex = 0;

                if (part.charAt(0) == '-') {
                    sign = -1;
                    startIndex = 1;
                }

                // поразрядное чтение числа
                for (int j = startIndex; j < part.length(); j++) {
                    char c = part.charAt(j);
                    if (c >= '0' && c <= '9') {
                        value = value * 10 + (c - '0');
                    } else {
                        throw new Exception("Некорректный символ в числе.");
                    }
                }
                arr[i] = value * sign;

            }
        } catch (Exception e) {
            System.out.println("Ошибка ввода: Введите только целые числа через пробел.");
            return;
        }

        FenwickTree ft = new FenwickTree(arr.length);
        ft.build(arr);

        // сохранение проекта в наши структуры
        trees[treeCount] = ft;
        arrays[treeCount] = arr;
        names[treeCount] = name;

        // новый проект становится текущим
        currentTreeIndex = treeCount;
        treeCount++;

        System.out.println("Проект массива '" + name + "' создан и выбран.");
    }

    private static void switchTree() {
        // проверяем, есть ли хотя бы один проект
        if (treeCount == 0) {
            System.out.println("Нет проектов массивов для переключения.");
            return;
        }

        // ввод имени проекта, на который нужно переключиться
        System.out.print("Введите имя проекта: ");
        String inputName = readLine();
        inputName = trimString(inputName);
        boolean found = false;

        // поиск проекта по имени
        for (int i = 0; i < treeCount; i++) {
            if (names[i] != null && names[i].equals(inputName)) {
                currentTreeIndex = i; // выбранный проект становится активным
                System.out.println("Переключено на проект '" + names[i] + "'.");
                found = true;
                break;
            }
        }
        // проект не найден
        if (!found) {
            System.out.println("Проект с таким именем не найден.");
        }
    }

    private static void deleteTree() {
        // проверка, есть ли хотя бы один проект для удаления
        if (treeCount == 0) {
            System.out.println("Нет проектов массивов для удаления.");
            return;
        }
        // запрос имени проекта для удаления
        System.out.print("Введите имя проекта для удаления: ");
        String inputName = readLine();
        inputName = trimString(inputName);// убираем лишние пробелы
        boolean found = false;

        // проходим по всем проектам
        for (int i = 0; i < treeCount; i++) {
            if (names[i] != null && names[i].equals(inputName)) {
                found = true;
                // сдвигаем все элементы справа от удаляемого влево, чтобы убрать пустое место
                for (int j = i; j < treeCount - 1; j++) {
                    trees[j] = trees[j + 1]; // сдвиг дерева
                    arrays[j] = arrays[j + 1];// сдвиг массива
                    names[j] = names[j + 1];// сдвиг имени проекта
                }
                trees[treeCount - 1] = null;
                arrays[treeCount - 1] = null;
                names[treeCount - 1] = null;

                treeCount--;
                // обновляем текущий выбранный проект
                if (currentTreeIndex == i) currentTreeIndex = treeCount > 0 ? 0 : -1; // если удаляли текущий, выбираем первый или -1, если проектов нет
                else if (currentTreeIndex > i) currentTreeIndex--;

                System.out.println("Проект '" + inputName + "' удален.");
                break;
            }
        }
        // если проект с таким именем не найден, выводим сообщение
        if (!found) {
            System.out.println("Проект с именем '" + inputName + "' не найден.");
        }
    }

    private static void runOperations(int treeIdx) {
        // получаем ссылку на текущее дерево и массив проекта
        FenwickTree ft = trees[treeIdx];
        int[] arr = arrays[treeIdx];
        boolean loop = true;

        // выводим меню операций для выбранного проекта
        while (loop) {
            System.out.print("\nТекущий массив: ");
            printArray(arr);
            System.out.println("\n--- Операции на массиве проекта '" + names[treeIdx] + "' ---");
            System.out.println("1. Сумма от 0 до индекса (Fenwick Tree)");
            System.out.println("2. Сумма на отрезке (Fenwick Tree)");
            System.out.println("3. Обновление элемента в массиве (Fenwick Tree обновляется)");
            System.out.println("4. Добавить элемент в массив по индексу (Fenwick Tree и массив обновляются)");
            System.out.println("5. Добавить элемент в конец массива (Fenwick Tree и массив обновляются)");
            System.out.println("6. Удалить элемент из массива по индексу (Fenwick Tree и массив обновляются)");
            System.out.println("7. Показать массив и Fenwick Tree");
            System.out.println("8. Найти K-й наименьший элемент в массиве (Fenwick Tree)");
            System.out.println("9. Выйти");
            System.out.print("Выбор: ");

            try {
                int choice = readInt();
                int index, left, right, value;
                long sumResult;

                switch (choice) {
                    // префиксная сумма от 0 до указанного индекса
                    case 1:
                        System.out.print("Индекс [0..." + (arr.length - 1) + "]: ");
                        index = readInt();
                        if (index < 0 || index >= arr.length) {
                            System.out.println("Индекс вне диапазона.");
                            break;
                        }
                        sumResult = ft.prefixSum(index);
                        System.out.println("Сумма = " + sumResult);
                        break;
                    case 2:
                        // сумма на отрезке [left, right]
                        System.out.print("Левая границв [0..." + (arr.length - 1) + "]: ");
                        left = readInt();
                        System.out.print("Правая граница [0..." + (arr.length - 1) + "]: ");
                        right = readInt();
                        if (left < 0 || right >= arr.length || left > right) {
                            System.out.println("Неверный диапазон индексов.");
                            break;
                        }
                        sumResult = ft.rangeSum(left, right);
                        System.out.println("Sum = " + sumResult);
                        break;
                    case 3:
                        // обновление элемента массива и пересчет дерева
                        System.out.print("Индекс элемента для обновления [0..." + (arr.length - 1) + "]: ");
                        index = readInt();
                        if (index < 0 || index >= arr.length) {
                            System.out.println("Индекс вне диапазона.");
                            break;
                        }
                        System.out.print("Новое значение: ");
                        value = readInt();

                        // дерево обновляется на разницу между новым и старым значением
                        ft.update(index, value - arr[index]);
                        arr[index] = value;
                        System.out.println("Элемент в массиве обновлен, Fenwick Tree пересчитан.");
                        break;
                    case 4:
                        // вставка элемента в массив по указанному индексу
                        System.out.print("Индекс для вставки [0..." + arr.length + "]: ");
                        index = readInt();
                        if (index < 0 || index > arr.length) {
                            System.out.println("Индекс вне диапазона.");
                            break;
                        }
                        System.out.print("Значение элемента: ");
                        value = readInt();

                        // создание нового массива с увеличенным размером и вставляем новый элемент
                        int[] newArr = new int[arr.length + 1];
                        for (int i = 0, j = 0; i < newArr.length; i++) {
                            if (i == index) newArr[i] = value;
                            else newArr[i] = arr[j++];
                        }
                        arr = newArr;
                        // пересоздаем дерево для нового массива
                        ft = new FenwickTree(arr.length);
                        ft.build(arr);
                        arrays[treeIdx] = arr;
                        trees[treeIdx] = ft;
                        System.out.println("Элемент добавлен в массив по индексу. Fenwick Tree перестроен.");
                        break;
                    case 5:
                        // добавление элемента в конец массива
                        appendElement(treeIdx, arr);
                        arr = arrays[treeIdx]; // обновляем ссылку на новый массив
                        ft = trees[treeIdx];// обновляем ссылку на дерево
                        break;
                    case 6:
                        // удаление элемента из массива по индексу
                        System.out.print("Индекс для удаления [0..." + (arr.length - 1) + "]: ");
                        index = readInt();
                        if (index < 0 || index >= arr.length) {
                            System.out.println("Индекс вне диапазона.");
                            break;
                        }
                        newArr = new int[arr.length - 1];
                        for (int i = 0, j = 0; i < arr.length; i++) {
                            if (i != index) newArr[j++] = arr[i];
                        }
                        arr = newArr;

                        ft = new FenwickTree(arr.length);
                        ft.build(arr);
                        arrays[treeIdx] = arr;
                        trees[treeIdx] = ft;
                        System.out.println("Элемент удалён из массива. Fenwick Tree перестроено.");
                        break;
                    case 7:
                        // показать текущий массив и состояние дерева
                        System.out.print("Array: ");
                        printArray(arr);
                        System.out.println("\nFenwick Tree: " + ft.getTreeState());
                        break;
                    case 8:
                        // поиск K-й наименьший элемент с использованием Frequency BIT
                        findKthElement(arr);
                        break;
                    case 9:
                        // выход из цикла операций
                        loop = false;
                        break;
                    default:
                        System.out.println("Неверный выбор.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка ввода. " + e.getMessage());
            }
        }
    }

    // добавление элемента в конец
    private static void appendElement(int treeIdx, int[] arr) throws Exception {
        System.out.print("Значение элемента для добавления в конец массива: ");
        int value = readInt();

        int[] newArr = new int[arr.length + 1];

        // копирование старого массива
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = arr[i];
        }
        // добавление нового элемента в конец
        newArr[arr.length] = value;

        // изменяем дерево
        FenwickTree ft = new FenwickTree(newArr.length);
        ft.build(newArr);

        arrays[treeIdx] = newArr;
        trees[treeIdx] = ft;
        System.out.println("Элемент '" + value + "' добавлен в конец массива. Fenwick Tree перестроено.");
    }


     //поиск к-го наименьшего элемента с использованием Frequency BIT.
    private static void findKthElement(int[] arr) {
        if (arr.length == 0) {
            System.out.println("Массив пуст.");
            return;
        }

        System.out.println("--- Поиск K-го наименьшего элемента ---");

        // находим K
        long k;
        try {
            System.out.print("Введите K (порядковый номер начиная с 1): ");
            k = readInt();
            if (k <= 0) {
                System.out.println("K должно быть положительным числом.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Ошибка ввода K. " + e.getMessage());
            return;
        }

        // находим мин/макс значения и вычисляем сдвиг
        int maxVal = arr[0];
        int minVal = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > maxVal) maxVal = arr[i];
            if (arr[i] < minVal) minVal = arr[i];
        }

        // сдвиг для работы с отрицательными числами
        int rebaseOffset = 0;
        if (minVal < 0) {
            rebaseOffset = -minVal;
            maxVal += rebaseOffset;
        }

        int bitSize = maxVal + 1;

        // создаем временное дерево для частот (Frequency BIT)
        FenwickTree frequencyFt = new FenwickTree(bitSize);

        //заполняем Frequency BIT
        for (int i = 0; i < arr.length; i++) {
            int rebasedValue = arr[i] + rebaseOffset;
            frequencyFt.update(rebasedValue, 1);
        }

        // поиск к-го элемента
        int rebasedResult = frequencyFt.findKthElement(k);

        // вывод результата
        if (rebasedResult == -1) {
            System.out.println("K вне диапазона. Общее количество элементов в массиве: " + arr.length);
        } else {
            // отменяем сдвиг, чтобы получить исходное значение
            int finalResult = rebasedResult - rebaseOffset;
            System.out.println("------------------------------------");
            System.out.println("Общее количество элементов в массиве: " + arr.length);
            System.out.println("Диапазон значений: от " + minVal + " до " + (maxVal - rebaseOffset));
            System.out.println(k + "-й наименьший элемент (значение) в массиве: " + finalResult);
            System.out.println("------------------------------------");
        }
    }
}