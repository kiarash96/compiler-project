int g(int z[]) {
    output(z[3]);
    return z[13];
}

int f(int x[], int y) {
    x[3] = y;
    return g(x);
}

void main(void) {
    int x[10];
    int y;
    x[13] = 133;
    y = 7;
    output(f(x, y));
    return;
}

