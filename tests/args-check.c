int f(int x, int y) {
    y = x + 2;
    return;
}

void g(void) {
    output(123);
    return 10;
}

int h(int x, int y[]) {
    return 555;
}

void main(void) {
    int x;
    int y[10];
    output(f(1, 2, 3));
    output(h(x, y));
    output(h(y, x));
    return;
}

