import pathlib, sys, unittest
sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent))
from fabricmeta import predicate_matches as m

class T(unittest.TestCase):
    def test_basic(self):
        self.assertTrue(m(">=0.15", "0.20.1"))
        self.assertFalse(m(">=0.21", "0.20.1"))
        self.assertTrue(m("*", "anything"))
        self.assertTrue(m("26.1.2", "26.1.2"))
    def test_tilde(self):
        self.assertTrue(m("~26.1.2", "26.1.2"))
        self.assertTrue(m("~26.1", "26.1.2"))
        self.assertFalse(m("~26.1", "26.2"))
        self.assertTrue(m("~26.1-", "26.1.2"))
    def test_prerelease_floor(self):
        self.assertTrue(m(">26.1-", "26.1.2"))
        self.assertTrue(m(">=26.1-", "26.1"))
        self.assertFalse(m("<26.1-", "26.1"))
    def test_and_or(self):
        self.assertTrue(m([">=2.2.1 <=3.0.0"], "2.2.1"))
        self.assertTrue(m([">=2.2.1", "<=3.0.0"], "9"))
        self.assertFalse(m(">=2.2.1 <=3.0.0", "3.1"))
    def test_build_meta_and_x(self):
        self.assertTrue(m(">=0.145.0+26.1.1", "0.155.3+26.1.2"))
        self.assertTrue(m("26.1.x", "26.1.2"))
        self.assertFalse(m("26.2.x", "26.1.2"))
        self.assertTrue(m(">=0.18.x+26.1", "0.18.3"))
    def test_caret(self):
        self.assertTrue(m("^5.0.0", "5.2"))
        self.assertFalse(m("^5.0.0", "6.0"))

if __name__ == "__main__":
    unittest.main()
