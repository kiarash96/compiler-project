int f(int x) {
    if (x < 10)
        return -1;
    else if (x == 10)
        return 0;
    else
        return +1;
}

void main(void) {
    output(f(66));
    return;
}

