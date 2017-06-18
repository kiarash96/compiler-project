int f(int x[], int y) {
    x[3] = y + 4;
    return x[5] * 7;
}

void main(void) {
    int x[10];
    int y;
    x[5] = 50;
    output(f(x, y));
    return;
}

