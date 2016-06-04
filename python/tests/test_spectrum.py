#!/usr/bin/env python
# -*- coding: utf-8 -*-

from splash import Spectrum, SpectrumType
import unittest


class TestSpectrumParser(unittest.TestCase):

    def test_spectrum_string(self):
        s = Spectrum("1:10 2:5 3:5", SpectrumType.MS)

        self.assertEqual(len(s.spectrum), 3)
        self.assertEqual(max(_[1] for _ in s.spectrum), 100.0)

    def test_spectrum_list(self):
        s = Spectrum([(1, 10), (2, 5), (3, 5)], SpectrumType.MS)

        self.assertEqual(len(s.spectrum), 3)
        self.assertEqual(max(_[1] for _ in s.spectrum), 100.0)

    def test_invalid_spectra(self):
        with self.assertRaises(ValueError):
            Spectrum('', SpectrumType.MS)

        with self.assertRaises(ValueError):
            Spectrum([], SpectrumType.MS)

        with self.assertRaises(ValueError):
            Spectrum('1 10 2 5 3 5', SpectrumType.MS)


if __name__ == '__main__':
    unittest.main()