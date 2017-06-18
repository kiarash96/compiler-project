int f(int x, int y) {
    y = x + 2;
    return;
}

void g(void) {
    output(123);
    return 10;
}

void main(void) {
    output(f(1, 2, 3));
    return;
}

