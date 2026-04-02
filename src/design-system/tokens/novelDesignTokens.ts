export const novelDesignLightTheme = {
  color: {
    bg: {
      canvas: "#FAF6F0",
      surface: "#FFFDFC",
      elevated: "#F3ECE3",
      surfaceMuted: "#F8F1E7"
    },
    text: {
      primary: "#201A17",
      secondary: "#6F6258",
      inverse: "#FFFDFC"
    },
    border: {
      subtle: "#E8DDD1",
      strong: "#C7B39F",
      focus: "#B85F2E"
    },
    brand: {
      primary: "#C96A34",
      secondary: "#8B4A2C",
      accent: "#D4A25A"
    },
    status: {
      success: "#4D7A52",
      warning: "#A16A1F",
      danger: "#B3453C"
    },
    interaction: {
      selected: "#F7E1D2",
      disabled: "#D6CCC2",
      focus: "#B85F2E"
    },
    reader: {
      emphasis: "#F0E1CF",
      chrome: "#EAD8C7"
    }
  },
  space: {
    "100": 8,
    "150": 12,
    "200": 16,
    "300": 24,
    "400": 32,
    "500": 40,
    "600": 48,
    "700": 64,
    "050": 4
  },
  radius: {
    sm: 8,
    md: 12,
    lg: 18,
    xl: 24,
    xxl: 32,
    full: 999
  },
  elevation: {
    "100": {
      x: 0,
      y: 2,
      blur: 8,
      spread: 0,
      alpha: 0.08
    },
    "200": {
      x: 0,
      y: 6,
      blur: 16,
      spread: 0,
      alpha: 0.12
    },
    "300": {
      x: 0,
      y: 12,
      blur: 28,
      spread: 0,
      alpha: 0.18
    }
  },
  motion: {
    duration: {
      fast: 120,
      normal: 200,
      slow: 320,
      page: 240,
      sheet: 280
    },
    curve: {
      standard: "cubic-bezier(0.2, 0, 0, 1)",
      decelerate: "cubic-bezier(0, 0, 0, 1)",
      accelerate: "cubic-bezier(0.3, 0, 1, 1)",
      entrance: "cubic-bezier(0.16, 1, 0.3, 1)"
    }
  },
  typography: {
    title: {
      hero: {
        fontFamily: "PingFangSC-Semibold",
        size: 32,
        lineHeight: 40,
        weight: 600,
        letterSpacing: -0.6
      },
      section: {
        fontFamily: "PingFangSC-Semibold",
        size: 24,
        lineHeight: 30,
        weight: 600,
        letterSpacing: -0.4
      }
    },
    body: {
      lg: {
        fontFamily: "PingFangSC-Regular",
        size: 18,
        lineHeight: 28,
        weight: 400,
        letterSpacing: 0
      },
      md: {
        fontFamily: "PingFangSC-Regular",
        size: 16,
        lineHeight: 24,
        weight: 400,
        letterSpacing: 0
      },
      sm: {
        fontFamily: "PingFangSC-Regular",
        size: 14,
        lineHeight: 20,
        weight: 400,
        letterSpacing: 0
      }
    },
    label: {
      md: {
        fontFamily: "PingFangSC-Medium",
        size: 14,
        lineHeight: 18,
        weight: 500,
        letterSpacing: 0.2
      },
      sm: {
        fontFamily: "PingFangSC-Medium",
        size: 12,
        lineHeight: 16,
        weight: 500,
        letterSpacing: 0.2
      }
    },
    nav: {
      md: {
        fontFamily: "PingFangSC-Medium",
        size: 14,
        lineHeight: 18,
        weight: 600,
        letterSpacing: 0.1
      }
    },
    meta: {
      md: {
        fontFamily: "PingFangSC-Medium",
        size: 12,
        lineHeight: 18,
        weight: 500,
        letterSpacing: 0.2
      },
      sm: {
        fontFamily: "PingFangSC-Medium",
        size: 11,
        lineHeight: 16,
        weight: 500,
        letterSpacing: 0.2
      }
    },
    eyebrow: {
      md: {
        fontFamily: "PingFangSC-Medium",
        size: 11,
        lineHeight: 16,
        weight: 700,
        letterSpacing: 0.6
      }
    },
    quote: {
      md: {
        fontFamily: "PingFangSC-Medium",
        size: 15,
        lineHeight: 24,
        weight: 500,
        letterSpacing: 0.1
      }
    },
    numeric: {
      lg: {
        fontFamily: "PingFangSC-Semibold",
        size: 24,
        lineHeight: 28,
        weight: 700,
        letterSpacing: -0.3
      }
    },
    reader: {
      chapter: {
        fontFamily: "PingFangSC-Medium",
        size: 22,
        lineHeight: 34,
        weight: 500,
        letterSpacing: 0.1
      },
      content: {
        fontFamily: "PingFangSC-Regular",
        size: 19,
        lineHeight: 34,
        weight: 400,
        letterSpacing: 0.1
      }
    }
  }
} as const;

export const novelDesignDarkTheme = {
  color: {
    bg: {
      canvas: "#161311",
      surface: "#211C19",
      elevated: "#2B2521",
      surfaceMuted: "#26201C"
    },
    text: {
      primary: "#F5EEE7",
      secondary: "#B8AA9D",
      inverse: "#161311"
    },
    border: {
      subtle: "#3A302B",
      strong: "#8D7865",
      focus: "#F0A06F"
    },
    brand: {
      primary: "#E08B56",
      secondary: "#C47049",
      accent: "#E7BA74"
    },
    status: {
      success: "#80B887",
      warning: "#D9A94B",
      danger: "#E0756C"
    },
    interaction: {
      selected: "#56382B",
      disabled: "#51453D",
      focus: "#F0A06F"
    },
    reader: {
      emphasis: "#3B2F2A",
      chrome: "#312824"
    }
  },
  space: {
    "100": 8,
    "150": 12,
    "200": 16,
    "300": 24,
    "400": 32,
    "500": 40,
    "600": 48,
    "700": 64,
    "050": 4
  },
  radius: {
    sm: 8,
    md: 12,
    lg: 18,
    xl: 24,
    xxl: 32,
    full: 999
  },
  elevation: {
    "100": {
      x: 0,
      y: 2,
      blur: 8,
      spread: 0,
      alpha: 0.08
    },
    "200": {
      x: 0,
      y: 6,
      blur: 16,
      spread: 0,
      alpha: 0.12
    },
    "300": {
      x: 0,
      y: 12,
      blur: 28,
      spread: 0,
      alpha: 0.18
    }
  },
  motion: {
    duration: {
      fast: 120,
      normal: 200,
      slow: 320,
      page: 240,
      sheet: 280
    },
    curve: {
      standard: "cubic-bezier(0.2, 0, 0, 1)",
      decelerate: "cubic-bezier(0, 0, 0, 1)",
      accelerate: "cubic-bezier(0.3, 0, 1, 1)",
      entrance: "cubic-bezier(0.16, 1, 0.3, 1)"
    }
  },
  typography: {
    title: {
      hero: {
        fontFamily: "PingFangSC-Semibold",
        size: 32,
        lineHeight: 40,
        weight: 600,
        letterSpacing: -0.6
      },
      section: {
        fontFamily: "PingFangSC-Semibold",
        size: 24,
        lineHeight: 30,
        weight: 600,
        letterSpacing: -0.4
      }
    },
    body: {
      lg: {
        fontFamily: "PingFangSC-Regular",
        size: 18,
        lineHeight: 28,
        weight: 400,
        letterSpacing: 0
      },
      md: {
        fontFamily: "PingFangSC-Regular",
        size: 16,
        lineHeight: 24,
        weight: 400,
        letterSpacing: 0
      },
      sm: {
        fontFamily: "PingFangSC-Regular",
        size: 14,
        lineHeight: 20,
        weight: 400,
        letterSpacing: 0
      }
    },
    label: {
      md: {
        fontFamily: "PingFangSC-Medium",
        size: 14,
        lineHeight: 18,
        weight: 500,
        letterSpacing: 0.2
      },
      sm: {
        fontFamily: "PingFangSC-Medium",
        size: 12,
        lineHeight: 16,
        weight: 500,
        letterSpacing: 0.2
      }
    },
    nav: {
      md: {
        fontFamily: "PingFangSC-Medium",
        size: 14,
        lineHeight: 18,
        weight: 600,
        letterSpacing: 0.1
      }
    },
    meta: {
      md: {
        fontFamily: "PingFangSC-Medium",
        size: 12,
        lineHeight: 18,
        weight: 500,
        letterSpacing: 0.2
      },
      sm: {
        fontFamily: "PingFangSC-Medium",
        size: 11,
        lineHeight: 16,
        weight: 500,
        letterSpacing: 0.2
      }
    },
    eyebrow: {
      md: {
        fontFamily: "PingFangSC-Medium",
        size: 11,
        lineHeight: 16,
        weight: 700,
        letterSpacing: 0.6
      }
    },
    quote: {
      md: {
        fontFamily: "PingFangSC-Medium",
        size: 15,
        lineHeight: 24,
        weight: 500,
        letterSpacing: 0.1
      }
    },
    numeric: {
      lg: {
        fontFamily: "PingFangSC-Semibold",
        size: 24,
        lineHeight: 28,
        weight: 700,
        letterSpacing: -0.3
      }
    },
    reader: {
      chapter: {
        fontFamily: "PingFangSC-Medium",
        size: 22,
        lineHeight: 34,
        weight: 500,
        letterSpacing: 0.1
      },
      content: {
        fontFamily: "PingFangSC-Regular",
        size: 19,
        lineHeight: 34,
        weight: 400,
        letterSpacing: 0.1
      }
    }
  }
} as const;
