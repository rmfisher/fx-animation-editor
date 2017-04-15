package animator.component.util.icon;

import javafx.scene.Node;

public enum Svg {

    BOUNCE(12.0D, 12.0D, SvgLayerData.of("M 6 0 C 5.446 0 5 0.446 5 1 L 5 11 C 5 11.554 5.446 12 6 12 C 6.554 12 7 11.554 7 11 L 7 1 C 7 0.446 6.554 0 6 0 z M 11 0 C 10.446 0 10 0.446 10 1 L 10 7 C 10 7.554 10.446 8 11 8 C 11.554 8 12 7.554 12 7 L 12 1 C 12 0.446 11.554 0 11 0 z M 1 8 C 0.446 8 0 8.4460001 0 9 L 0 11 C 0 11.554 0.446 12 1 12 C 1.554 12 2 11.554 2 11 L 2 9 C 2 8.4460001 1.554 8 1 8 z ", "#000000")),
    EASE_BOTH(12.0D, 12.0D, SvgLayerData.of("M 1 0 C 0.446 0 0 0.44600004 0 1 L 0 11 C 0 11.554 0.446 12 1 12 C 1.554 12 2 11.554 2 11 L 2 1 C 2 0.44600004 1.554 0 1 0 z M 11 0 C 10.446 0 10 0.446 10 1 L 10 11 C 10 11.554 10.446 12 11 12 C 11.554 12 12 11.554 12 11 L 12 1 C 12 0.446 11.554 0 11 0 z M 6 6 C 5.446 6 5 6.446 5 7 L 5 11 C 5 11.554 5.446 12 6 12 C 6.554 12 7 11.554 7 11 L 7 7 C 7 6.446 6.554 6 6 6 z ", "#000000")),
    EASE_IN(12.0D, 12.0D, SvgLayerData.of("M 1 0 C 0.446 0 0 0.446 0 1 L 0 11 C 0 11.554 0.446 12 1 12 C 1.554 12 2 11.554 2 11 L 2 1 C 2 0.446 1.554 0 1 0 z M 6 4 C 5.446 4 5 4.446 5 5 L 5 11 C 5 11.554 5.446 12 6 12 C 6.554 12 7 11.554 7 11 L 7 5 C 7 4.446 6.554 4 6 4 z M 11 8 C 10.446 8 10 8.446 10 9 L 10 11 C 10 11.554 10.446 12 11 12 C 11.554 12 12 11.554 12 11 L 12 9 C 12 8.446 11.554 8 11 8 z ", "#000000")),
    EASE_OUT(12.0D, 12.0D, SvgLayerData.of("M 11 0 C 10.446 0 10 0.446 10 1 L 10 11 C 10 11.554 10.446 12 11 12 C 11.554 12 12 11.554 12 11 L 12 1 C 12 0.446 11.554 0 11 0 z M 6 4 C 5.446 4 5 4.446 5 5 L 5 11 C 5 11.554 5.446 12 6 12 C 6.554 12 7 11.554 7 11 L 7 5 C 7 4.446 6.554 4 6 4 z M 1 8 C 0.446 8 0 8.446 0 9 L 0 11 C 0 11.554 0.446 12 1 12 C 1.554 12 2 11.554 2 11 L 2 9 C 2 8.446 1.554 8 1 8 z ", "#000000")),
    LINEAR(12.0D, 12.0D, SvgLayerData.of("M 1 0 C 0.446 0 0 0.44600004 0 1 L 0 11 C 0 11.554 0.446 12 1 12 C 1.554 12 2 11.554 2 11 L 2 1 C 2 0.44600004 1.554 0 1 0 z M 6 0 C 5.446 0 5 0.446 5 1 L 5 11 C 5 11.554 5.446 12 6 12 C 6.554 12 7 11.554 7 11 L 7 1 C 7 0.446 6.554 0 6 0 z M 11 0 C 10.446 0 10 0.446 10 1 L 10 11 C 10 11.554 10.446 12 11 12 C 11.554 12 12 11.554 12 11 L 12 1 C 12 0.446 11.554 0 11 0 z ", "#000000")),
    MINUS_TINY(7.0D, 7.0D, SvgLayerData.of("M 0 3 L 0 4 L 7 4 L 7 3 L 0 3 z ", "#000000")),
    OVERSHOOT(12.0D, 12.0D, SvgLayerData.of("M 6 0 C 5.446 0 5 0.446 5 1 L 5 11 C 5 11.554 5.446 12 6 12 C 6.554 12 7 11.554 7 11 L 7 1 C 7 0.446 6.554 0 6 0 z M 11 4 C 10.446 4 10 4.446 10 5 L 10 11 C 10 11.554 10.446 12 11 12 C 11.554 12 12 11.554 12 11 L 12 5 C 12 4.446 11.554 4 11 4 z M 1 8 C 0.446 8 0 8.446 0 9 L 0 11 C 0 11.554 0.446 12 1 12 C 1.554 12 2 11.554 2 11 L 2 9 C 2 8.446 1.554 8 1 8 z ", "#000000")),
    PLUS(9.0D, 9.0D, SvgLayerData.of("M 4.5 0 C 4.223 0 4 0.24390625 4 0.546875 L 4 4 L 0.546875 4 C 0.24390625 4 0 4.223 0 4.5 C 0 4.777 0.24390625 5 0.546875 5 L 4 5 L 4 8.453125 C 4 8.7560938 4.223 9 4.5 9 C 4.777 9 5 8.7560938 5 8.453125 L 5 5 L 8.453125 5 C 8.7560938 5 9 4.777 9 4.5 C 9 4.223 8.7560938 4 8.453125 4 L 5 4 L 5 0.546875 C 5 0.24390625 4.777 0 4.5 0 z ", "#000000")),
    PLUS_FAT(10.0D, 10.0D, SvgLayerData.of("M 4.6933594 0 C 4.5039649 0 4.3435152 0.066685417 4.2109375 0.19921875 C 4.0783653 0.33188542 4.0117188 0.49230729 4.0117188 0.68164062 L 4.0117188 3.9960938 L 0.68164062 3.9960938 C 0.49224618 3.9960938 0.33179097 4.0627125 0.19921875 4.1953125 C 0.06664097 4.3279125 0 4.4883344 0 4.6777344 L 0 5.3066406 C 0 5.4960406 0.06664097 5.6564625 0.19921875 5.7890625 C 0.33179097 5.9216625 0.49224618 5.9882812 0.68164062 5.9882812 L 4.0117188 5.9882812 L 4.0117188 9.3183594 C 4.0117188 9.5076927 4.0783653 9.6681146 4.2109375 9.8007812 C 4.3435152 9.9333812 4.5039649 10 4.6933594 10 L 5.3222656 10 C 5.5116601 10 5.6721098 9.9333812 5.8046875 9.8007812 C 5.9372597 9.6681146 6.0039062 9.5076927 6.0039062 9.3183594 L 6.0039062 5.9882812 L 9.3183594 5.9882812 C 9.5077538 5.9882812 9.6682091 5.9216625 9.8007812 5.7890625 C 9.9333591 5.6564625 10 5.4960406 10 5.3066406 L 10 4.6777344 C 10 4.4883344 9.933359 4.3279125 9.8007812 4.1953125 C 9.6682091 4.0627125 9.5077538 3.9960938 9.3183594 3.9960938 L 6.0039062 3.9960938 L 6.0039062 0.68164062 C 6.0039063 0.49230729 5.9372597 0.33188542 5.8046875 0.19921875 C 5.6721098 0.066685417 5.5116601 0 5.3222656 0 L 4.6933594 0 z ", null)),
    SPLINE(12.0D, 12.0D, SvgLayerData.of("M 1 0 C 0.446 0 0 0.446 0 1 C 0 1.554 0.446 2 1 2 C 1.554 2 2 1.554 2 1 C 2 0.446 1.554 0 1 0 z M 11 0 C 10.446 0 10 0.446 10 1 L 10 11 C 10 11.554 10.446 12 11 12 C 11.554 12 12 11.554 12 11 L 12 1 C 12 0.446 11.554 0 11 0 z M 1 4 C 0.446 4 0 4.4460001 0 5 L 0 11 C 0 11.554 0.446 12 1 12 C 1.554 12 2 11.554 2 11 L 2 5 C 2 4.4460001 1.554 4 1 4 z M 6 6 C 5.446 6 5 6.446 5 7 L 5 11 C 5 11.554 5.446 12 6 12 C 6.554 12 7 11.554 7 11 L 7 7 C 7 6.446 6.554 6 6 6 z ", "#000000")),
    PAUSE(10.0D, 10.0D, SvgLayerData.of("M 0.41601562 0 C 0.30316895 0 0.20551358 0.041046875 0.12304688 0.12304688 C 0.040580205 0.20554687 0 0.30311562 0 0.41601562 L 0 9.5839844 C 0 9.6968844 0.040580205 9.7944531 0.12304688 9.8769531 C 0.20551355 9.9596531 0.30316896 10 0.41601562 10 L 3.578125 10 C 3.6909717 10 3.7886268 9.9589531 3.8710938 9.8769531 C 3.9535607 9.7944531 3.9941406 9.6968844 3.9941406 9.5839844 L 3.9941406 0.41601562 C 3.9941406 0.30311563 3.9535607 0.20504688 3.8710938 0.12304688 C 3.7886271 0.040346875 3.6909717 0 3.578125 0 L 0.41601562 0 z M 6.4375 0 C 6.3246533 0 6.2269983 0.041046875 6.1445312 0.12304688 C 6.0620642 0.20554687 6.0214844 0.30311562 6.0214844 0.41601562 L 6.0214844 9.5839844 C 6.0214844 9.6968844 6.0620642 9.7949531 6.1445312 9.8769531 C 6.226998 9.9596531 6.3246533 10 6.4375 10 L 9.5839844 10 C 9.6968311 10 9.7944861 9.9589531 9.8769531 9.8769531 C 9.9594201 9.7944531 10 9.6968844 10 9.5839844 L 10 0.41601562 C 10 0.30311563 9.9594201 0.20504688 9.8769531 0.12304688 C 9.7944864 0.040346875 9.6968311 0 9.5839844 0 L 6.4375 0 z ", null)),
    PLAY(10.0D, 10.0D, SvgLayerData.of("M 0.24609375 0 C 0.19930375 -0.0031666667 0.1562775 0.0060104167 0.1171875 0.02734375 C 0.0390075 0.070277083 0 0.14809896 0 0.25976562 L 0 9.7402344 C 0 9.8519677 0.0390075 9.9280365 0.1171875 9.9707031 C 0.1953675 10.014036 0.2894575 10.009125 0.3984375 9.953125 L 9.8359375 5.1992188 C 9.9449175 5.1433521 10 5.0772667 10 5 C 10 4.9226667 9.9449175 4.8567813 9.8359375 4.8007812 L 0.3984375 0.046875 C 0.3439475 0.019208333 0.29288375 0.0031666667 0.24609375 0 z ", null)),
    STOP(10.0D, 10.0D, SvgLayerData.of("M 0.4375 0 C 0.3246533 0 0.22699825 0.041046875 0.14453125 0.12304688 C 0.06206425 0.20554687 0.021484375 0.30311562 0.021484375 0.41601562 L 0.021484375 9.5839844 C 0.021484375 9.6968844 0.06206425 9.7949531 0.14453125 9.8769531 C 0.22699795 9.9596531 0.3246533 10 0.4375 10 L 9.5839844 10 C 9.6968311 10 9.7944861 9.9589531 9.8769531 9.8769531 C 9.9594201 9.7944531 10 9.6968844 10 9.5839844 L 10 0.41601562 C 10 0.30311563 9.9594201 0.20504688 9.8769531 0.12304688 C 9.7944864 0.040346875 9.6968311 0 9.5839844 0 L 0.4375 0 z ", null)),
    WARNING(40.0D, 40.0D, SvgLayerData.of("M 20.03125 1.1894531 C 19.511004 1.1894531 19.027957 1.3225906 18.582031 1.5878906 C 18.136111 1.8532906 17.787849 2.2129219 17.535156 2.6699219 L 0.41210938 33.810547 C -0.10815732 34.739547 -0.094895175 35.667003 0.45507812 36.595703 C 0.70777143 37.023303 1.0536968 37.362681 1.4921875 37.613281 C 1.9306782 37.863981 2.4028218 37.990234 2.9082031 37.990234 L 37.154297 37.990234 C 37.659678 37.990234 38.131822 37.863981 38.570312 37.613281 C 39.008803 37.362681 39.354729 37.023303 39.607422 36.595703 C 40.157374 35.667003 40.17259 34.739547 39.652344 33.810547 L 22.529297 2.6699219 C 22.276604 2.2129219 21.928342 1.8532906 21.482422 1.5878906 C 21.036496 1.3225906 20.551496 1.1894531 20.03125 1.1894531 z M 17.578125 12.513672 L 22.484375 12.513672 C 22.647879 12.513672 22.826299 12.593659 23.019531 12.755859 C 23.168177 12.859059 23.242188 12.998781 23.242188 13.175781 L 22.841797 23.326172 C 22.826937 23.473672 22.748637 23.597559 22.607422 23.693359 C 22.466216 23.789359 22.292086 23.835938 22.083984 23.835938 L 17.958984 23.835938 C 17.750883 23.835938 17.570515 23.789159 17.421875 23.693359 C 17.273235 23.597359 17.201172 23.473672 17.201172 23.326172 L 16.820312 13.220703 C 16.820312 13.014303 16.896274 12.859159 17.044922 12.755859 C 17.238154 12.593759 17.414621 12.513672 17.578125 12.513672 z M 17.890625 26.666016 L 22.171875 26.666016 C 22.365107 26.666016 22.532623 26.737053 22.673828 26.876953 C 22.815039 27.017053 22.884766 27.190184 22.884766 27.396484 L 22.884766 31.599609 C 22.884766 31.806009 22.815036 31.977087 22.673828 32.117188 C 22.532623 32.257188 22.365107 32.328125 22.171875 32.328125 L 17.890625 32.328125 C 17.697393 32.328125 17.529877 32.257188 17.388672 32.117188 C 17.247461 31.977087 17.177734 31.806009 17.177734 31.599609 L 17.177734 27.396484 C 17.177734 27.190184 17.247462 27.017053 17.388672 26.876953 C 17.529877 26.736853 17.697393 26.666016 17.890625 26.666016 z ", null));

    private final SvgData data;

    Svg(double width, double height, SvgLayerData... layers) {
        this.data = new SvgData(name(), width, height, layers);
    }

    public SvgData data() {
        return data;
    }

    public Node node() {
        return new SvgView(data).getRoot();
    }
}
