function copyCode() {
    const button = event.target.closest("button")
    const iconElement = button.querySelector("i")

    iconElement.classList.add("bounce")
    navigator.clipboard.writeText("317610").then(() => {
        setTimeout(() => {
            iconElement.textContent = "check"
            iconElement.className = "material-icons text-green-600 copy-icon success"
        }, 250)
        setTimeout(() => {
            iconElement.textContent = "content_copy"
            iconElement.className = "material-icons copy-icon"
            iconElement.style.color = "hsl(var(--primary))"
        }, 1500)
    })
}

function toggleStatuts() {
    const dropdownElement = document.getElementById("statutsDropdown")
    dropdownElement.classList.toggle("dropdown-open")
}

function toggleMobileMenu() {
    const mobileMenuElement = document.getElementById("mobile-menu")
    const mobileMenuAnonymousElement = document.getElementById("mobile-menu-anonymous")

    if (mobileMenuElement) {
        mobileMenuElement.classList.toggle("hidden")
    }

    if (mobileMenuAnonymousElement) {
        mobileMenuAnonymousElement.classList.toggle("hidden")
    }
}

function handleResize() {
    const mobileMenuElement = document.getElementById("mobile-menu")
    const mobileMenuAnonymousElement = document.getElementById("mobile-menu-anonymous")

    if (window.innerWidth >= 768) {
        if (mobileMenuElement) {
            mobileMenuElement.classList.add("hidden")
        }
        if (mobileMenuAnonymousElement) {
            mobileMenuAnonymousElement.classList.add("hidden")
        }
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const mobileMenuButtons = document.querySelectorAll("header button.md\\:hidden")
    mobileMenuButtons.forEach(buttonElement => {
        buttonElement.addEventListener("click", toggleMobileMenu)
    })

    window.addEventListener("resize", handleResize)

    document.querySelectorAll("form").forEach(form => {
        form.addEventListener("submit", () => {
            const submitBtn = form.querySelector("button[type='submit']")
            if (submitBtn) {
                submitBtn.disabled = true
                submitBtn.innerHTML = '<i class="material-icons text-sm mr-2 align-middle">hourglass_top</i> <span>Traitement en cours...</span>'
            }
        })
    })
})
