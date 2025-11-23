class FenwickTree {

    // внутреннее представление дерева (используется long для предотвращения переполнения)
    private long[] tree;
    private int size;

    private int lsb(int i) {
        return i & (-i);
    }
    //конструктор Fenwick Tree
    public FenwickTree(int arraySize) {
        if (arraySize < 0) {
            this.size = 0;
            this.tree = new long[1];
        } else {
            this.size = arraySize;
            this.tree = new long[arraySize + 1];
        }
    }

    // построение дерева по исходному массиву
    public void build(int[] arr) {
        if (arr == null) {
            this.size = 0;
            this.tree = new long[1];
            return;
        }

        if (arr.length != this.size) {
            this.size = arr.length;
            this.tree = new long[this.size + 1];
        } else {
            for (int i = 0; i < this.tree.length; i++) {
                this.tree[i] = 0;
            }
        }

        // при построении суммарного BIT, обновляем каждый элемент
        for (int i = 0; i < this.size; i++) {
            updateInternal(i, arr[i]);
        }
    }

    //внутреннее обновление дерева по индексу с дельтой
    private void updateInternal(int index, int delta) {
        if (index < 0 || index >= this.size) return;

        long value = delta;
        int i = index + 1; // переход к 1 индексации
        while (i <= this.size) {
            this.tree[i] += value;
            i += lsb(i);
        }
    }
    //публичный метод обновления элемента массива на дельту
    public void update(int index, int delta) {
        updateInternal(index, delta);
    }

    //сумма элементов от 0 до index включительно (используется тип long, чтобы избежать переполнения при больших суммах)
    public long prefixSum(int index) {
        if (index < 0) return 0;
        if (index >= this.size) index = this.size - 1;

        int i = index + 1; // переход к 1 индексации
        long sum = 0;
        while (i > 0) {
            sum += this.tree[i];
            i -= lsb(i);
        }
        return sum;
    }

    // вычисление суммы на отрезке [left, right] (используется тип long, чтобы избежать переполнения при больших суммах)
    public long rangeSum(int left, int right) {
        if (left > right || right >= this.size || left < 0) return 0;
        return prefixSum(right) - prefixSum(left - 1);
    }

    // поиск к-го наименьшего элемента в массиве, если BIT используется как частотная таблица
    // используется бинарный поиск по префиксным суммам
    public int findKthElement(long k) {
        if (k <= 0) return -1;
        long totalCount = prefixSum(this.size - 1);
        if (k > totalCount) return -1;
        int low = 0;
        int high = this.size - 1;
        int result = -1;

        // бинарный поиск по индексам
        while (low <= high) {
            int mid = low + (high - low) / 2;
            long count = prefixSum(mid);

            if (count >= k) {
                result = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return result;
    }

    // возвращает текущее состояние дерева в виде строки
    public String getTreeState() {
        if (this.size == 0) return "[] (Дерево пусто)";

        String result = "[";
        for (int i = 1; i <= this.size; i++) {
            result += this.tree[i];
            if (i < this.size) {
                result += ", ";
            }
        }
        result += "]";
        return result;
    }

    public int getSize() {
        return this.size;
    }
}