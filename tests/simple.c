int x;

int func(void) {
    return 10 * x;
}

int main(void) {
    x = 1 + 2;
    output(func());
    x = 3;
    x = func();
    return 0;
}
