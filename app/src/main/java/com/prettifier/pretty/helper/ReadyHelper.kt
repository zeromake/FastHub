package com.prettifier.pretty.helper

class ReadyHelper {
    companion object {
        const val SCRIPT = """
        <script type="application/javascript">
			var listener = function() {
				document.removeEventListener("DOMContentLoaded", listener, false);
				if (typeof android !== "undefined") {
					android.resize(document.body.getBoundingClientRect().height);
				} else {
					console.log("height", document.body.getBoundingClientRect().height);
				}
			};
			document.addEventListener("DOMContentLoaded", listener, false);
		</script>
        """
    }
}