<details>
	<summary>Emergency?</summary>
	<p>If you are in a crisis, call or text<br><a href="tel:988">988</a><br>or chat<br><a href="https://988lifeline.org/chat/">https://988lifeline.org/chat/</a></p>
	<p>If you are in a medical emergency or suicidal crisis and you cannot connect, please call 911.</p>
</details>

<style lang="scss">
	details {
		margin: 0;
		padding: 0.5rem;
		border: 1px solid var(--dark2);
		border-radius: 15px;
		background: var(--natural4);
		color: var(--dark2);
		width: fit-content;
		&::marker{
			display: none;
		}
		&[open]{
			position: absolute;
			z-index: 51;
			top: 0;
			right: 0;
			margin: 1rem var(--side-spacing);
		}
	}

	summary {
		display: block;
		font: 700 0.875rem var(--ff);
		cursor: pointer;
	}

	a {
		font-size: 1.5em;
		text-decoration: underline;
	}
</style>